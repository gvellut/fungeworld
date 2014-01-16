package com.vellut.fungeworld.mason;

import com.vellut.fungeworld.Utils;
import com.vellut.fungeworld.lang.Instruction;

public class BoardIO {

	private int pcReadError, pcWriteError;
	private MutationStrategy mutationStrategy;

	public BoardIO(MutationStrategy mutationStrategy, int pcReadError,
			int pcWriteError) {
		this.pcReadError = pcReadError;
		this.pcWriteError = pcWriteError;
		this.mutationStrategy = mutationStrategy;
	}

	public Instruction read(Simulation sim, int[] memoryCell) {
		Instruction instruction = (Instruction) sim.instructionGrid.field[memoryCell[0]][memoryCell[1]];
		if (pcReadError > sim.random.nextInt(100)) {
			// Introduce temporary error in the stack of the calling process
			instruction = mutationStrategy.mutate(sim, instruction);
		}
		return instruction;
	}

	public void write(Simulation sim, int[] memoryCell, Instruction value) {
		// Introduce error in the board
		if (pcWriteError > sim.random.nextInt(100)) {
			value = mutationStrategy.mutate(sim, value);
		}
		sim.instructionGrid.field[memoryCell[0]][memoryCell[1]] = value;
	}
	
	public void writeRandom(Simulation sim, int[] memoryCell, int maxIntegerValue) {
		Instruction value = Utils.randomInstruction(sim.random, maxIntegerValue);
		sim.instructionGrid.field[memoryCell[0]][memoryCell[1]] = value;
	}


}
