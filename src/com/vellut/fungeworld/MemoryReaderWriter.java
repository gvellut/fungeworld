package com.vellut.fungeworld;

public interface MemoryReaderWriter {

  void read(int[] memoryCell);
  void write(int[] memoryCell, Instruction value);
  void waitUntilWrite(int[] memoryCell);
  
}
