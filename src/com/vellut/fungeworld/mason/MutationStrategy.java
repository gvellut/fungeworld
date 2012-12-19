package com.vellut.fungeworld.mason;

import com.vellut.fungeworld.Instruction;

public interface MutationStrategy {
	Instruction mutate(Simulation sim, Instruction instruction);
}
