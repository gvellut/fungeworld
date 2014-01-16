package com.vellut.fungeworld.lang;

import java.util.HashMap;
import java.util.Map;

public class InstructionCache {

	private static Instruction[] instructions;
	private static Map<InstructionType, Integer> instructionIndex;
	private static Map<String, InstructionType> instructionRepresentationDictionary;

	static {
		// TODO change this so the index for an isntructionType is the same
		// no matter the order of addition of new instructionTypes
		InstructionType[] instructionTypes = InstructionType.values();
		instructions = new Instruction[instructionTypes.length];
		instructionIndex = new HashMap<>();
		instructionRepresentationDictionary = new HashMap<>();

		for (int i = 0; i < instructions.length; i++) {
			InstructionType instructionType = instructionTypes[i];
			Instruction instruction = new Instruction(instructionType);
			if (instructionType == InstructionType.INTEGER) {
				instruction.setAttachedData(0);
			}

			instructionRepresentationDictionary.put(
					instructionType.getRepresentation(), instructionType);
			instructionIndex.put(instructionType, i);
			instructions[i] = instruction;
		}
	}

	public static Instruction[] getInstructions() {
		return instructions;
	}

	public static Map<InstructionType, Integer> getInstructionIndex() {
		return instructionIndex;
	}

	public static Map<String, InstructionType> getInstructionRepresentationDictionary() {
		return instructionRepresentationDictionary;
	}

}
