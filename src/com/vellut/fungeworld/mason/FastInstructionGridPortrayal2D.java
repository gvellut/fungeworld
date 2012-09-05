package com.vellut.fungeworld.mason;

import sim.portrayal.grid.FastObjectGridPortrayal2D;

import com.vellut.fungeworld.Instruction;

// Use this to display the instruction grid
public class FastInstructionGridPortrayal2D extends
		FastObjectGridPortrayal2D {

	@Override
	public double doubleValue(Object obj) {
		Instruction instruction = (Instruction) obj;

		// FIXME see Java 7 multi-case syntax
		switch (instruction.getInstructionType()) {
		case ABS_DPOS_DECR_0:
		case ABS_DPOS_DECR_1:
		case ABS_DPOS_INCR_0:
		case ABS_DPOS_INCR_1:
			// IP Delta manipulation operations
			return 0;
		case ABS_DPOS_COND_0:
		case ABS_DPOS_COND_1:
			// Conditional operations
			return 1;
		case ADD:
		case DIV:
		case MUL:
		case SUB:
		case MOD:
		case NOT:
			// Arithmetic operations
			return 2;
		case EQ:
		case GT:
			// Comparison operations
			return 3;
		case DUP:
		case SWAP:
		case POP:
			// Stack operations
			return 4;
		case READ:
		case WRITE:
			// IO operations
			return 5;
		case BRIDGE:
			// IP Manipulation operations
			return 6;
		case INTEGER:
			return 7;
		case END:
			return 8;
		default:
			return 9;
		}
	}

}
