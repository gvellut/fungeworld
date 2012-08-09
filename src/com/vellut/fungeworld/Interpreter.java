package com.vellut.fungeworld;

import java.util.Stack;

public class Interpreter {
	private int dimension;
	private int[] instructionPointer;
	private int[] deltaInstructionPointer;
	private MemoryReaderWriter memoryReaderWriter;
	private InterpreterState state;
	private Stack<Instruction> executionStack;

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

			case DUP:
				// Duplicate top element of the stack
				if(!executionStack.empty()) {
					executionStack.push(executionStack.peek());
				} // else like noop
				break;
			case SWAP:
				// Swap top 2 elements of the stack
				if(executionStack.size() >= 2) {
					Instruction instr1 = executionStack.pop();
					Instruction instr2 = executionStack.pop();
					executionStack.push(instr1);
					executionStack.push(instr2);
				} // else like noop
				break;
			case POP:
				if(!executionStack.empty()) {
					executionStack.pop();
				} // else like noop
				break;
			case BRIDGE:
				// Jumps IP one more
				incrementInstructionPointer();
				break;
			case ADD:
				if(executionStack.size() >= 2) {
					int op1 = getIntegerOperand();
					int op2 = getIntegerOperand();
					pushInteger(op1 + op2);
				} // else like noop
				break;
			case SUB:
				if(executionStack.size() >= 2) {
					int op1 = getIntegerOperand();
					int op2 = getIntegerOperand();
					pushInteger(op2 - op1);
				} // else like noop
				break;
			case DIV:
				if(executionStack.size() >= 2) {
					int op1 = getIntegerOperand();
					int op2 = getIntegerOperand();
					pushInteger(op2 / op1);
				} // else like noop
				break;
			case MUL:
				if(executionStack.size() >= 2) {
					int op1 = getIntegerOperand();
					int op2 = getIntegerOperand();
					pushInteger(op2 * op1);
				} // else like noop
				break;
			case EQ:
				if(executionStack.size() >= 2) {
					// Can be performed on more than just integers
					// (although int version 1 it is all there is)
					Instruction instr1 = executionStack.pop();
					Instruction instr2 = executionStack.pop();
					boolean eq = instr1.equals(instr2);
					pushInteger(eq?1:0);
				} // else like noop
				break;
			case GT:
				if(executionStack.size() >= 2) {
					int op1 = getIntegerOperand();
					int op2 = getIntegerOperand();
					pushInteger(op2 > op1?1:0);
				} // else like noop
				break;
			case NOT:
				if(!executionStack.empty()) {
					int op1 = getIntegerOperand();
					pushInteger(op1 == 0?1:0);
				} // else like noop
				break;
			case INTEGER:
				executionStack.push(instr);
				break;
			case ABS_DPOS_INCR_0:
				updateInstructionPointer(0, 1);
				break;
			case ABS_DPOS_DECR_0:
				updateInstructionPointer(0, -1);
				break;
			case ABS_DPOS_INCR_1:
				updateInstructionPointer(1, 1);
				break;
			case ABS_DPOS_DECR_1:
				updateInstructionPointer(1, -1);
				break;
			case ABS_DPOS_COND_0:
				if(!executionStack.empty()) {
					int op1 = getIntegerOperand();
					updateInstructionPointer(0, op1 == 0?1:-1);
				} // else like noop
				break;
			case ABS_DPOS_COND_1:
				if(!executionStack.empty()) {
					int op1 = getIntegerOperand();
					updateInstructionPointer(1, op1 == 0?1:-1);
				} // else like noop
				break;
			case END:
				this.state = InterpreterState.KILLED;
				break;
			case READ:
				if(!executionStack.empty()) {
					int y = getIntegerOperand();
					int x = getIntegerOperand();
					this.state = InterpreterState.EXPECTING_READ_RESPONSE;
					memoryReaderWriter.read(new int[]{x,y});
				}
				break;
			case WRITE:
				if(!executionStack.empty()) {
					int y = getIntegerOperand();
					int x = getIntegerOperand();
					Instruction instrValue = executionStack.pop();
					memoryReaderWriter.write(new int[]{x,y}, instrValue);
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
	
	private void updateInstructionPointer(int dimension, int deltaValue) {
		instructionPointer[dimension] += deltaValue;
	}
	
	private int getIntegerOperand() {
		Instruction instr1 = executionStack.pop();
		// 0 is the default values in case the value on the stack
		// is not an integer
		int op1 = 0;
		if(instr1.getInstructionType() == InstructionType.INTEGER) {
			op1 = (Integer) instr1.getAttachedData();
		}
		return op1;
	}
	
	private void pushInteger(int value) {
		Instruction result = new Instruction(InstructionType.INTEGER);
		result.setAttachedData(value);
		executionStack.push(result);
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
