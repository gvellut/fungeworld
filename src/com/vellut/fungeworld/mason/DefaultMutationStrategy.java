package com.vellut.fungeworld.mason;

import com.vellut.fungeworld.lang.Instruction;
import com.vellut.fungeworld.lang.InstructionType;

public class DefaultMutationStrategy implements MutationStrategy {

	private int pcFullError;

	public DefaultMutationStrategy(int pcFullError) {
		this.pcFullError = pcFullError;
	}

	@Override
	// TODO instead of mutating during IO, maybe mutate the interpretation of an
	// instruction
	// ie an instruction could be interpreted in multiple ways eg DUP could
	// duplicate n times instead of always 1
	// TODO introduce compound instructions (multiple instructions executed
	// in one step; or like an array). Need to introduce new instructions to
	// manage them. eg mutation of > could be (!;>) (instead of <)
	// TODO instead of mutating instruction, mutate the state of the interpreter
	// eg the direction or ignore change of direction randomly or pointer or
	// mutate the change of pointer
	// TODO introduce strategy pattern to allow testing of different mutation
	// possibilities
	public Instruction mutate(Simulation sim, Instruction instruction) {
		// Random error part of the times
		if (pcFullError > sim.random.nextInt(100)) {
			return MutationStrategyUtils.randomInstruction(sim);
		}

		switch (instruction.getInstructionType()) {

		case DUP: {
			// Random integer in [-5, 5]
			Instruction newInstr = new Instruction(InstructionType.INTEGER);
			newInstr.setAttachedData(sim.random.nextInt(11) - 5);
			return newInstr;
		}
		case SWAP:
			return MutationStrategyUtils.randomInstruction(sim);
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
			return MutationStrategyUtils.randomInstruction(sim);
		case REL_DPOS_LEFT:
			return new Instruction(InstructionType.REL_DPOS_RIGHT);
		case REL_DPOS_RIGHT:
			return new Instruction(InstructionType.REL_DPOS_LEFT);
		case REL_DPOS_COMPARE:
			if (sim.random.nextBoolean()) {
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
			return MutationStrategyUtils.randomInstruction(sim);
		}
	}

}
