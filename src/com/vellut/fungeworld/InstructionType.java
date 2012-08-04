package com.vellut.fungeworld;

public enum InstructionType {
  DUP("Dup"),
  SWAP("Swap"),
  ADD("+"),
  SUB("-"),
  MUL("*"),
  DIV("/"),
  MOD("%"),
  EQ("="),
  GT(">"),
  INTEGER("Int"),
  NOOP("Noop");
  
  private String representation;
  
  private InstructionType(String representation) {
    this.representation = representation;
  }
  
  public String toString() {
    return representation;
  }
  
}
