package com.vellut.fungeworld;

import java.util.Stack;

public class Interpreter {
  private int dimension;
  private int[] instructionPointer;
  private int[] deltaInstructionPointer;
  private MemoryReaderWriter memoryReaderWriter;
  private InterpreterState state;
  private Stack<Instruction> executionStack; 
  
  public Interpreter(int dimension, MemoryReaderWriter memoryReaderWriter) {
    this.dimension = dimension;
    this.memoryReaderWriter = memoryReaderWriter;
    this.executionStack = new Stack<Instruction>();
  }
  
  public void setInitialState(int[] instructionPointer, int[] deltaInstructionPointer) 
    throws InterpreterException{
    if(instructionPointer.length != dimension) {
      throw new InterpreterException("Length of IP not equal to dimension (" + 
          dimension +")");
    }
    this.instructionPointer = instructionPointer;
    
    if(deltaInstructionPointer.length != dimension) {
      throw new InterpreterException("Length of Delta-IP not equal to dimension (" + 
          dimension +")");
    }
    this.deltaInstructionPointer = deltaInstructionPointer;
    
    state = InterpreterState.RUNNING;
  }
  
  // In case the IP has gone beyond the limits of the game Matrix,
  // this can be called to correct it
  public void correctInstructionPointer(int[] instructionPointer) {
    this.instructionPointer = instructionPointer;
  }
  
  // In case timeout for a read reached
  public void correctState(InterpreterState state) {
    this.state = state;
  }
  
  public void executeInstruction(Instruction instr) throws InterpreterException {
    if(state == InterpreterState.RUNNING) {

      switch(instr.getInstructionType()) {
      case ADD:
        
        break;
      case SUB:
        
        break;
      case DIV:
        
        break;  
        
      // For READ make change of state before calling read
      default:
          
      }
      
      incrementInstructionPointer();
    }
    
    throw new InterpreterException("Bad state: Expected " + 
        InterpreterState.EXPECTING_READ_RESPONSE); 
  }
  
  private void incrementInstructionPointer() {
    for(int i = 0 ; i < dimension ; i++) {
      instructionPointer[i] += deltaInstructionPointer[i];
    }
  }
  
  // When read response received, this method must be called by the MemoeryReadWriter 
  // to notify the interpreter
  public void onReadResponse(Instruction instr) throws InterpreterException {
    if(state == InterpreterState.EXPECTING_READ_RESPONSE) {
      executionStack.push(instr);
      state = InterpreterState.RUNNING;
    }
    throw new InterpreterException("Bad state: Expected " + 
        InterpreterState.EXPECTING_READ_RESPONSE); 
  }
  
  // When a wait for a write on a memory location is set, this method must 
  // be called by the MemoeryReaderWriter to notify the interpreter when 
  // the memoery location is written
  public void onWaitRelease(Instruction instr)  throws InterpreterException {
    if(state == InterpreterState.WAITING_FOR_WRITE_RELEASE) {
      executionStack.push(instr);
      state = InterpreterState.RUNNING;
    }
    throw new InterpreterException("Bad state: Expected " + 
        InterpreterState.WAITING_FOR_WRITE_RELEASE); 
  }

  public MemoryReaderWriter getMemoryReaderWriter() {
    return memoryReaderWriter;
  }

  public void setMemoryReaderWriter(MemoryReaderWriter memoryReaderWriter) {
    this.memoryReaderWriter = memoryReaderWriter;
  }

  public int getDimension() {
    return dimension;
  }

  public int[] getInstructionPointer() {
    return instructionPointer;
  }

  public int[] getDeltaInstructionPointer() {
    return deltaInstructionPointer;
  }

  public InterpreterState getState() {
    return state;
  }
  
}
