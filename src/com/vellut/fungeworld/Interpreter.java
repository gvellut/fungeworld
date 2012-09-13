package com.vellut.fungeworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Interpreter {
	// Always 2 (for now)
	private int dimension;
	private int[] instructionPointer;
	private int[] deltaInstructionPointer;
	private BoardProxy boardProxy;
	private InterpreterState state;
	private Stack<Instruction> executionStack;
	private List<Instruction> output;
	// The I/O operations are relative to this origin
	private int[] relativeOrigin;

	private static final Instruction DEFAULT_OPERAND;

	private enum InterpreterMode {
		QUOTE_MODE, ITERATE_MODE
	};

	static {
		DEFAULT_OPERAND = new Instruction(InstructionType.INTEGER);
		DEFAULT_OPERAND.setAttachedData(Integer.valueOf(0));
	}

	public Interpreter(int dimension, BoardProxy boardProxy) {
		this.dimension = dimension;
		this.boardProxy = boardProxy;
		this.executionStack = new Stack<Instruction>();
	}

	public void setInitialState(int[] instructionPointer,
			int[] deltaInstructionPointer) throws InterpreterException {
		if (instructionPointer.length != dimension) {
			throw new InterpreterException(
					"Length of IP not equal to dimension (" + dimension + ")");
		}
		this.instructionPointer = instructionPointer;

		if (deltaInstructionPointer.length != dimension) {
			throw new InterpreterException(
					"Length of Delta-IP not equal to dimension (" + dimension
							+ ")");
		}
		this.deltaInstructionPointer = deltaInstructionPointer;

		// Origin is set to initial IP address value
		relativeOrigin = Arrays.copyOf(instructionPointer,
				instructionPointer.length);
		state = InterpreterState.RUNNING;
	}

	public void captureOutput(boolean flag) {
		if (flag) {
			output = new ArrayList<>();
		} else {
			output = null;
		}
	}

	// In case timeout for a read reached
	public void correctState(InterpreterState state) {
		this.state = state;
	}

	public void executeInstruction(Instruction instr)
			throws InterpreterException {
		if (state == InterpreterState.RUNNING) {
			boolean incr = runInstruction(instr);

			if (incr) {
				incrementInstructionPointer();
			}
		} else if (state == InterpreterState.QUOTE) {
			pushInstruction(instr);
			state = InterpreterState.RUNNING;
			incrementInstructionPointer();
		}
	}

	// FIXME when using vectors, make sure the correct number
	// of arguments are popped (according to the dimension of
	// the interpreter)
	// Returns flag that indicates if DeltaIP must be incremented
	private boolean runInstruction(Instruction instr) {
		switch (instr.getInstructionType()) {

		case DUP: {
			// Duplicate top element of the stack
			// If stack is empty: two '0' are pushed
			Instruction op = popOperand();
			pushInstruction(op);
			pushInstruction(op);
		}
			break;
		case SWAP: {
			// Swap top 2 elements of the stack
			// If stack is empty: two '0' are pushed
			Instruction instr1 = popOperand();
			Instruction instr2 = popOperand();
			pushInstruction(instr1);
			pushInstruction(instr2);
		}
			break;
		case POP:
			popOperand(); // ignore
			break;
		case BRIDGE:
			// Jumps IP twice
			incrementInstructionPointer();
			incrementInstructionPointer();
			return false;
		case ADD: {
			// Default value is 0 in case stack empty or bad type
			int op1 = popIntegerOperand();
			int op2 = popIntegerOperand();
			pushInteger(op1 + op2);
		}
			break;
		case SUB: {
			int op1 = popIntegerOperand();
			int op2 = popIntegerOperand();
			pushInteger(op2 - op1);
		}
			break;
		case DIV: {
			int op1 = popIntegerOperand();
			int op2 = popIntegerOperand();
			if (op1 != 0) {
				pushInteger(op2 / op1);
			} else {
				if (op2 == 0) {
					pushInteger(1);
				} else {
					pushInteger(0);
				}
			}
		}
			break;
		case MUL: {
			int op1 = popIntegerOperand();
			int op2 = popIntegerOperand();
			pushInteger(op2 * op1);
		}
			break;
		case MOD: {
			int op1 = popIntegerOperand();
			int op2 = popIntegerOperand();
			pushInteger(op2 % op1);
		}
			break;
		case EQ: {
			// Can be performed on more than just integers
			Instruction instr1 = popOperand();
			Instruction instr2 = popOperand();
			boolean eq = instr1.equals(instr2);
			pushInteger(eq ? 1 : 0);
		}
			break;
		case GT: {
			int op1 = popIntegerOperand();
			int op2 = popIntegerOperand();
			pushInteger(op2 > op1 ? 1 : 0);
		}
			break;
		case NOT: {
			int op1 = popIntegerOperand();
			pushInteger(op1 == 0 ? 1 : 0);
		}
			break;
		case INTEGER:
			executionStack.push(instr);
			break;
		case ABS_DPOS_INCR_0:
			updateDeltaInstructionPointer(0, 1);
			break;
		case ABS_DPOS_DECR_0:
			updateDeltaInstructionPointer(0, -1);
			break;
		case ABS_DPOS_INCR_1:
			updateDeltaInstructionPointer(1, 1);
			break;
		case ABS_DPOS_DECR_1:
			updateDeltaInstructionPointer(1, -1);
			break;
		case ABS_DPOS_COND_0: {
			int op1 = popIntegerOperand();
			updateDeltaInstructionPointer(0, op1 == 0 ? 1 : -1);
		}
			break;
		case ABS_DPOS_COND_1: {
			int op1 = popIntegerOperand();
			updateDeltaInstructionPointer(1, op1 == 0 ? 1 : -1);
		}
			break;
		case END:
			state = InterpreterState.KILLED;
			break;
		case READ: {
			int y = popIntegerOperand();
			int x = popIntegerOperand();
			state = InterpreterState.EXPECTING_READ_RESPONSE;
			boardProxy.read(new int[] { relativeOrigin[0] + x,
					relativeOrigin[1] + y });
		}
			break;
		case WRITE: {
			int y = popIntegerOperand();
			int x = popIntegerOperand();
			Instruction instrValue = popOperand();
			boardProxy.write(new int[] { relativeOrigin[0] + x,
					relativeOrigin[1] + y }, instrValue);
		}
			break;
		case SPAWN: {
			int y = popIntegerOperand();
			int x = popIntegerOperand();
			boardProxy.spawn(new int[] { relativeOrigin[0] + x,
					relativeOrigin[1] + y });
		}
			break;
		case REL_DPOS_LEFT: {
			// Turn 90deg left
			rotateDeltaInstructionPointer(-1);
		}
			break;
		case REL_DPOS_RIGHT: {
			// Turn 90deg right
			rotateDeltaInstructionPointer(1);
		}
			break;
		case REL_DPOS_COMPARE: {
			int op1 = popIntegerOperand();
			int op2 = popIntegerOperand();
			if (op2 > op1) {
				rotateDeltaInstructionPointer(1);
			} else if (op1 > op2) {
				rotateDeltaInstructionPointer(-1);
			}
		}
			break;
		case INTEGER_TO_INSTRUCTION: {
			Instruction instr1 = popOperand();
			// We do some checks: If operand not valid (stack empty, not an int
			// or int value not valid), we always push
			// the default operand (instead of depending on the order
			// of the instructions in the InstructionCache)
			if (instr1 != DEFAULT_OPERAND
					&& instr1.getInstructionType() == InstructionType.INTEGER) {
				int op1 = (Integer) instr1.getAttachedData();
				Instruction[] instructions = InstructionCache.getInstructions();
				if (op1 >= 0 && op1 < instructions.length) {
					Instruction push = instructions[op1];
					pushInstruction(push);
				} else {
					pushInstruction(DEFAULT_OPERAND);
				}
			} else {
				// push default
				pushInstruction(DEFAULT_OPERAND);
			}
		}
			break;
		case INSTRUCTION_TO_INTEGER: {
			Instruction instr1 = popOperand();
			int push = InstructionCache.getInstructionIndex().get(
					instr1.getInstructionType());
			pushInteger(push);
		}
			break;
		case QUOTE: {
			state = InterpreterState.QUOTE;
		}
			break;
		case JUMP: {
			int op1 = popIntegerOperand();
			// No effect if op1 <= 0
			// same as bridge if op1 == 2
			for (int i = 0; i < op1; i++) {
				incrementInstructionPointer();
			}
			return false;
		}
		case ABS_DPOS: {
			int y = popIntegerOperand();
			int x = popIntegerOperand();
			deltaInstructionPointer[0] = x;
			deltaInstructionPointer[1] = y;
		}
			break;
		case REL_DPOS_REVERSE: {
			deltaInstructionPointer[0] *= -1;
			deltaInstructionPointer[1] *= -1;
		}
			break;
		case EXEC: {
			// Runs the instruction on top of the stack
			Instruction instr1 = popOperand();
			return runInstruction(instr1);
		}
		case CLEAR: {
			executionStack.clear();
		}
			break;
		case PRINT: {
			Instruction opInstr = popOperand();
			if (output != null) {
				output.add(opInstr);
			} else {
				System.out.println(opInstr);
			}
		}
			break;
		default: // case NOOP :

		}

		return true;
	}

	private void incrementInstructionPointer() {
		for (int i = 0; i < dimension; i++) {
			instructionPointer[i] += deltaInstructionPointer[i];
		}
	}
	
	private void updateDeltaInstructionPointer(int dimension, int deltaValue) {
		for (int i = 0; i < deltaInstructionPointer.length; i++) {
			deltaInstructionPointer[i] = 0;
		}
		deltaInstructionPointer[dimension] = deltaValue;
	}
	
	// Rotation can only be -1 (-90deg) or 1 (+90deg)
	private void rotateDeltaInstructionPointer(int direction) {
		int a = deltaInstructionPointer[0];
		int b = deltaInstructionPointer[1];

		deltaInstructionPointer[0] = direction * -b;
		deltaInstructionPointer[1] = direction * a;
	}

	private int popIntegerOperand() {
		if (executionStack.empty()) {
			return 0;
		}

		Instruction instr1 = executionStack.pop();
		// 0 is the default values in case the value on the stack
		// is not an integer
		int op1 = 0;
		if(instr1.getInstructionType() == InstructionType.INTEGER) {
			op1 = (Integer) instr1.getAttachedData();
		}
		return op1;
	}
	
	private Instruction popOperand() {
		if (executionStack.empty()) {
			return DEFAULT_OPERAND;
		}
		return executionStack.pop();
	}

	private void pushInteger(int value) {
		Instruction result = new Instruction(InstructionType.INTEGER);
		result.setAttachedData(value);
		executionStack.push(result);
	}

	private void pushInstruction(Instruction instr) {
		executionStack.push(instr);
	}

	// When read response received, this method must be called by the
	// BoardProxy to notify the interpreter
	public void onReadResponse(Instruction instr) throws InterpreterException {
		if (state == InterpreterState.EXPECTING_READ_RESPONSE) {
			executionStack.push(instr);
			state = InterpreterState.RUNNING;
		} else {
			throw new InterpreterException("Bad state: Expected "
					+ InterpreterState.EXPECTING_READ_RESPONSE);
		}
	}

	// When a wait for a write on a memory location is set, this method must
	// be called by the BoardProxy to notify the interpreter when
	// the memory location is written
	public void onWaitRelease(Instruction instr) throws InterpreterException {
		if (state == InterpreterState.WAITING_FOR_WRITE_RELEASE) {
			executionStack.push(instr);
			state = InterpreterState.RUNNING;
		} else {
			throw new InterpreterException("Bad state: Expected "
					+ InterpreterState.WAITING_FOR_WRITE_RELEASE);
		}
	}

	public BoardProxy getboardProxy() {
		return boardProxy;
	}

	public void setBoardProxy(BoardProxy boardProxy) {
		this.boardProxy = boardProxy;
	}

	public List<Instruction> getOutput() {
		if (output != null) {
			return output;
		} else {
			return null;
		}
	}

	public int getDimension() {
		return dimension;
	}

	public int[] getInstructionPointer() {
		return instructionPointer;
	}

	public int[] getDeltaInstructionPointer() {
		return deltaInstructionPointer;
	}

	public InterpreterState getState() {
		return state;
	}

	public Stack<Instruction> getExecutionStack() {
		return executionStack;
	}

}
