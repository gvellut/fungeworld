package com.vellut.fungeworld;

public enum InterpreterState {
  // No expecting write confirmation
  RUNNING, EXPECTING_READ_RESPONSE, WAITING_FOR_WRITE_RELEASE, KILLED;
}