package com.vellut.fungeworld.io;

import com.vellut.fungeworld.lang.Instruction;

public interface InstructionGridFiller {
	void fillLine(Instruction[][] program, int lineIndex, int startColumn);
}
