package com.vellut.fungeworld;

import com.vellut.fungeworld.lang.Instruction;
import com.vellut.fungeworld.lang.InstructionType;

import ec.util.MersenneTwisterFast;

public class Utils {

	public static Instruction randomInstruction(MersenneTwisterFast random,
			int maxIntegerValue) {
		InstructionType[] instructionTypes = InstructionType.values();
		int index = random.nextInt(instructionTypes.length);
		InstructionType instructionType = instructionTypes[index];

		if (instructionType == InstructionType.SPAWN) {
			// replace 80% with dup so not spawned too often
			if (random.nextInt(10) < 8) {
				instructionType = InstructionType.NOOP;
			}
		}

		Instruction instruction = new Instruction(instructionType);

		// Only integers and spawns can have attached data
		if (instructionType == InstructionType.INTEGER) {
			int intValue = random.nextInt(maxIntegerValue);
			instruction.setAttachedData(intValue);
		}
		
		if(instructionType == InstructionType.SPAWN) {
			int intValue = random.nextInt(20);
			instruction.setAttachedData(intValue);
		}

		return instruction;
	}
}
