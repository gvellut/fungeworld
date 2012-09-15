package com.vellut.fungeworld.mason;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.InstructionCache;
import com.vellut.fungeworld.InstructionType;

public class BoardIO {

	private int pcReadError, pcWriteError, pcFullError;

	public BoardIO(int pcReadError, int pcWriteError, int pcFullError) {
		this.pcReadError = pcReadError;
		this.pcWriteError = pcWriteError;
		this.pcFullError = pcFullError;
	}

	public Instruction read(Simulation sim, int[] memoryCell) {
		Instruction instruction = (Instruction) sim.instructionGrid.field[memoryCell[0]][memoryCell[1]];
		if (pcReadError > sim.random.nextInt(100)) {
			// Introduce temporary error in the stack of the calling process
			instruction = mutate(sim, instruction);
		}
		return instruction;
	}

	public void write(Simulation sim, int[] memoryCell, Instruction value) {
		// Introduce error in the board
		if (pcWriteError > sim.random.nextInt(100)) {
			value = mutate(sim, value);
		}
		sim.instructionGrid.field[memoryCell[0]][memoryCell[1]] = value;
	}

	// TODO instead of mutating during IO, maybe mutate the interpretation of an
	// instruction
	// ie an instruction could be interpreted in multiple ways eg DUP could
	// duplicate n times instead of always 1
	// TODO introduce compound instructions (multiple instructions executed
	// in one step; or like an array). Need to introduce new instructions to
	// manage them. eg mutation of > could be (!;>) (instead of <)
	private Instruction mutate(Simulation sim, Instruction instruction) {
		// Random error part of the times
		if(pcFullError > sim.random.nextInt(100)) {
			return randomInstruction(sim);
		}
		
		switch(instruction.getInstructionType()) {
		
		case DUP: {
			// Random integer in [-5, 5]
			Instruction newInstr = new Instruction(InstructionType.INTEGER);
			newInstr.setAttachedData(sim.random.nextInt(11) - 5);
			return newInstr;
		}
		case SWAP: 
			return randomInstruction(sim);
		case POP:
			// no mutation
			return instruction;
		case BRIDGE:
			return new Instruction(InstructionType.JUMP);
		case ADD: 
			return new Instruction(InstructionType.SUB);
		case SUB: 
			return new Instruction(InstructionType.DIV);
		case DIV: 
			return new Instruction(InstructionType.MUL);
		case MUL:
			return new Instruction(InstructionType.MOD);
		case MOD: 
			return new Instruction(InstructionType.ADD);
		case EQ: 
			return new Instruction(InstructionType.GT);
		case GT: 
			return new Instruction(InstructionType.EQ);
		case NOT:
			return new Instruction(InstructionType.NOOP);
		case INTEGER: {
			// Drift the integer at random of [-3, 3]
			int data = (Integer) instruction.getAttachedData();
			int delta = sim.random.nextInt(7) - 3;
			Instruction newInstr = new Instruction(InstructionType.INTEGER);
			newInstr.setAttachedData(data + delta);
			return newInstr;
		}
		case ABS_DPOS_INCR_0:
			return new Instruction(InstructionType.ABS_DPOS_DECR_0);
		case ABS_DPOS_DECR_0:
			return new Instruction(InstructionType.ABS_DPOS_INCR_0);
		case ABS_DPOS_INCR_1:
			return new Instruction(InstructionType.ABS_DPOS_DECR_1);
		case ABS_DPOS_DECR_1:
			return new Instruction(InstructionType.ABS_DPOS_INCR_1);
		case ABS_DPOS_COND_0:
			return new Instruction(InstructionType.ABS_DPOS_COND_1);
		case ABS_DPOS_COND_1:
			return new Instruction(InstructionType.ABS_DPOS_COND_0);
		case END:
			return randomInstruction(sim);
		case REL_DPOS_LEFT: 
			return new Instruction(InstructionType.REL_DPOS_RIGHT);
		case REL_DPOS_RIGHT: 
			return new Instruction(InstructionType.REL_DPOS_LEFT);
		case REL_DPOS_COMPARE: 
			if(sim.random.nextBoolean()) {
				return new Instruction(InstructionType.ABS_DPOS_COND_0);
			} else {
				return new Instruction(InstructionType.ABS_DPOS_COND_1);
			}
		case INTEGER_TO_INSTRUCTION:
			// no mutation
			return instruction;
		case INSTRUCTION_TO_INTEGER:
			return new Instruction(InstructionType.EXEC);
		case QUOTE:
			return new Instruction(InstructionType.NOOP);
		case JUMP:
			return new Instruction(InstructionType.BRIDGE);
		case ABS_DPOS:
			return new Instruction(InstructionType.SPAWN);
		case REL_DPOS_REVERSE:
			return new Instruction(InstructionType.NOOP);
		case EXEC:
			return new Instruction(InstructionType.INSTRUCTION_TO_INTEGER);
		case CLEAR:
			return new Instruction(InstructionType.POP);
		default:
			return randomInstruction(sim);
		}
	}

	private Instruction randomInstruction(Simulation sim) {
		Instruction[] instructions = InstructionCache.getInstructions();
		return instructions[sim.random.nextInt(instructions.length)];
	}

}
