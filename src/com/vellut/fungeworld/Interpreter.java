package com.vellut.fungeworld;

import java.util.Stack;

public class Interpreter {
	private int dimension;
	private int[] instructionPointer;
	private int[] deltaInstructionPointer;
	private MemoryReaderWriter memoryReaderWriter;
	private InterpreterState state;
	private Stack<Instruction> executionStack;

	private static final Instruction DEFAULT_OPERAND;

	static {
		DEFAULT_OPERAND = new Instruction(InstructionType.INTEGER);
		DEFAULT_OPERAND.setAttachedData(Integer.valueOf(0));
	}

	public Interpreter(int dimension, MemoryReaderWriter memoryReaderWriter) {
		this.dimension = dimension;
		this.memoryReaderWriter = memoryReaderWriter;
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

		state = InterpreterState.RUNNING;
	}

	// In case the IP has gone beyond the limits of the game Matrix,
	// this can be called to correct it
	public void correctInstructionPointer(int[] instructionPointer) {
		this.instructionPointer = instructionPointer;
	}

	// In case timeout for a read reached
	public void correctState(InterpreterState state) {
		this.state = state;
	}

	public void executeInstruction(Instruction instr)
			throws InterpreterException {
		if (state == InterpreterState.RUNNING) {

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
				// Jumps IP one more
				incrementInstructionPointer();
				break;
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
			case EQ: {
				// Can be performed on more than just integers
				// (although int version 1 it is all there is)
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
				this.state = InterpreterState.KILLED;
				break;
			case READ: {
				int y = popIntegerOperand();
				int x = popIntegerOperand();
				this.state = InterpreterState.EXPECTING_READ_RESPONSE;
				memoryReaderWriter.read(new int[] { x, y });
			}
				break;
			case WRITE: {
				int y = popIntegerOperand();
				int x = popIntegerOperand();
				Instruction instrValue = popOperand();
				memoryReaderWriter.write(new int[] { x, y }, instrValue);
			}
				break;
			default:

			}

			if(this.state != InterpreterState.KILLED) {
				incrementInstructionPointer();
			}
		}
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
	// MemoeryReadWriter
	// to notify the interpreter
	public void onReadResponse(Instruction instr) throws InterpreterException {
		if (state == InterpreterState.EXPECTING_READ_RESPONSE) {
			executionStack.push(instr);
			state = InterpreterState.RUNNING;
		}
		throw new InterpreterException("Bad state: Expected "
				+ InterpreterState.EXPECTING_READ_RESPONSE);
	}

	// When a wait for a write on a memory location is set, this method must
	// be called by the MemoeryReaderWriter to notify the interpreter when
	// the memory location is written
	public void onWaitRelease(Instruction instr) throws InterpreterException {
		if (state == InterpreterState.WAITING_FOR_WRITE_RELEASE) {
			executionStack.push(instr);
			state = InterpreterState.RUNNING;
		}
		throw new InterpreterException("Bad state: Expected "
				+ InterpreterState.WAITING_FOR_WRITE_RELEASE);
	}

	public MemoryReaderWriter getMemoryReaderWriter() {
		return memoryReaderWriter;
	}

	public void setMemoryReaderWriter(MemoryReaderWriter memoryReaderWriter) {
		this.memoryReaderWriter = memoryReaderWriter;
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

}
