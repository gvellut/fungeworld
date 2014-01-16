package com.vellut.fungeworld.mason;

import com.vellut.fungeworld.lang.Instruction;

public interface MutationStrategy {
	Instruction mutate(Simulation sim, Instruction instruction);
}
