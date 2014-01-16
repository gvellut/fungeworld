package com.vellut.fungeworld.mason;

import com.vellut.fungeworld.lang.Instruction;
import com.vellut.fungeworld.lang.InstructionCache;

public class MutationStrategyUtils {

	public static Instruction randomInstruction(Simulation sim) {
		Instruction[] instructions = InstructionCache.getInstructions();
		return instructions[sim.random.nextInt(instructions.length)];
	}
}
