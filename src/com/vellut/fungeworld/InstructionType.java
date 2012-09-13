package com.vellut.fungeworld;

public enum InstructionType {
	// Stack ops
	DUP(":"), SWAP("\\"), POP("$"), CLEAR("n"),

	// Delta IP ops
	BRIDGE("#"), JUMP("j"),

	// Arithmetic ops
	ADD("+"), SUB("-"), MUL("*"), DIV("/"), MOD("%"), EQ("="), GT("`"), NOT("!"),

	// Integer
	INTEGER("i"),

	// Absolute change of directions
	// Acts on the first 2 dimensions
	// 1st (X) for left/right; 2nd (Y) for up/down
	// Y points down (so incr Y is V)
	ABS_DPOS_INCR_0(">"), ABS_DPOS_DECR_0("<"), ABS_DPOS_INCR_1("v"), ABS_DPOS_DECR_1(
			"^"), ABS_DPOS_COND_0("_"), ABS_DPOS_COND_1("|"),

	// Sets the absolute deltaIP from a vector
	ABS_DPOS("d"),

	// Relative change of direction (acts on first 2 dimensions)
	REL_DPOS_LEFT("["), REL_DPOS_RIGHT("]"), REL_DPOS_COMPARE("w"),

	// Reverses the deltaIP
	REL_DPOS_REVERSE("r"),

	// I/O
	READ("g"), WRITE("p"),

	// Reflection : int to Instruction, Instruction to int
	INTEGER_TO_INSTRUCTION("q"), INSTRUCTION_TO_INTEGER("z"),

	// Various
	NOOP("."), END("@"), PRINT(","), QUOTE("\""), SPAWN("s"), EXEC("x");

	// TODO implement those ?
	// Add new instructions at the end (or can change meaning of
	// existing programs; See Instructioncache)
	// RANDOM_DPOS("?"), ITERATE("k"),

	private String representation;

	private InstructionType(String representation) {
		this.representation = representation;
	}

	public String toString() {
		return representation;
	}

	public String getRepresentation() {
		return representation;
	}

}
