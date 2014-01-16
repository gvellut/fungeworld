package com.vellut.fungeworld.mason;

import com.vellut.fungeworld.lang.Instruction;

public class NoMutationStrategy implements MutationStrategy {

	@Override
	public Instruction mutate(Simulation sim, Instruction instruction) {
		return instruction;
	}

}
