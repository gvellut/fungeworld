package com.vellut.fungeworld;

// Used for asynchronous IO on the board by the processes
public interface MemoryReaderWriter {

  void read(int[] memoryCell);
  void write(int[] memoryCell, Instruction value);
  void waitUntilWrite(int[] memoryCell);
  
}
