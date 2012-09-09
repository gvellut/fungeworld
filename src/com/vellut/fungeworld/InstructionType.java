package com.vellut.fungeworld;

public enum InstructionType {
	// Stack ops
	DUP(":"), SWAP("\\"), POP("$"), CLEAR("n"),

	// Delta IP ops
	BRIDGE("#"),

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

	// I/O
	READ("g"), WRITE("p"),

	// Various
	NOOP("."), END("@"), PRINT(",");

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
