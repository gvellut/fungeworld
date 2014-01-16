package com.vellut.fungeworld.io;

import com.vellut.fungeworld.Utils;
import com.vellut.fungeworld.lang.Instruction;

import ec.util.MersenneTwisterFast;

public class RandomFiller implements InstructionGridFiller {

	private MersenneTwisterFast random;

	public RandomFiller(MersenneTwisterFast random) {
		this.random = random;
	}

	@Override
	public void fillLine(Instruction[][] program, int lineIndex, int startColumn) {
		for (int restLine = startColumn; restLine < program.length; restLine++) {
			program[restLine][lineIndex] = Utils.randomInstruction(random,
					program.length);
		}
	}

}
