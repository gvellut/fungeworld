package com.vellut.fungeworld.io;

import com.vellut.fungeworld.lang.Instruction;
import com.vellut.fungeworld.lang.InstructionType;

public class NoopFiller implements InstructionGridFiller {
	
	Instruction noop = new Instruction(InstructionType.NOOP);
	
	@Override
	public void fillLine(Instruction[][] program, int lineIndex, int startColumn) {
		// Fill the rest with NOOP (so the whole rectangle is filled
		// with instructions)
		for (int restLine = startColumn; restLine < program.length; restLine++) {
			program[restLine][lineIndex] = noop;
		}
	}
}
