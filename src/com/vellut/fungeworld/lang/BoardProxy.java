package com.vellut.fungeworld.lang;

// Used for asynchronous IO on the board by the processes
public interface BoardProxy {
	void read(int[] memoryCell);
	void write(int[] memoryCell, Instruction value);
	void mutate(int[] memoryCell, Instruction value);
	void waitUntilWrite(int[] memoryCell);
	void spawn(int[] memoryCell, int[] delta);
}
