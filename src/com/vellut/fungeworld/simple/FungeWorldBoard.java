package com.vellut.fungeworld.simple;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.Interpreter;
import com.vellut.fungeworld.InterpreterException;
import com.vellut.fungeworld.InterpreterState;
import com.vellut.fungeworld.MemoryReaderWriter;

public class FungeWorldBoard implements MemoryReaderWriter {
  
  Instruction[][] board;
  int numRows, numCols;
  Interpreter interpreter;
  
  public FungeWorldBoard(int numRows, int numCols) {
    this.numRows = numRows;
    this.numCols = numCols;
    board = new Instruction[numRows][numCols];
    interpreter = new Interpreter(2, this);
  }
  
  // Load a rectangle of instructions at a certain coordinate
  public void load(Instruction[][] program, int[] coordinates) {
    
  }
  
  public void run(int[] instructionPointer, int[] deltaInstructionPointer) {
    
    try {
      interpreter.setInitialState(instructionPointer, deltaInstructionPointer);
    } catch (InterpreterException e) {
      System.out.println("Bad initialization parameters");
      return;
    }
      
    while(interpreter.getState() != InterpreterState.KILLED) {
      try {
        int[] currentIP = interpreter.getInstructionPointer();
        if(!isIndexValid(currentIP)) {
          currentIP = correctIndex(currentIP);
          interpreter.correctInstructionPointer(currentIP);
        }
        Instruction instr = getData(currentIP);
        interpreter.executeInstruction(instr);
      } catch (InterpreterException e) {
          System.out.println(e.getMessage());
          // FIXME output board for checking
          return;
      }
    }
  }
  
  private Instruction getData(int[] index) {
    return board[index[0]][index[1]];
  }
  
  private boolean isIndexValid(int[] index) {
    int rows = index[0];
    int cols = index[1];
    return (rows < numRows && cols < numCols);
  }
  
  private int[] correctIndex(int[] index) {
    int[] indexCorr = new int[2];
    indexCorr[0] = index[0] % numRows;
    indexCorr[1] = index[1] % numCols;
    return indexCorr;
  }
  
  @Override 
  public String toString() {
    // FIXME output state of the interpreter
    // FIXME output board
    return null;
  }
  
  @Override
  public void read(int[] index) {
    try {
      // Just read the board
      if(!isIndexValid(index)) {
        index = correctIndex(index);
      }
      interpreter.onReadResponse(getData(index));
    } catch (InterpreterException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void write(int[] index, Instruction value) {
    if(!isIndexValid(index)) {
      index = correctIndex(index);
    }
    board[index[0]][index[1]] = value;
  }

  @Override
  public void waitUntilWrite(int[] memoryCell) {
    // Will never be called (only one interpreter on this board)
  }

}
