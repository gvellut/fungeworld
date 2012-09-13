package com.vellut.fungeworld;

import java.util.HashMap;
import java.util.Map;

public class InstructionCache {

	public static Instruction[] instructions;
	public static Map<InstructionType, Integer> instructionIndex;
	public static Map<String, InstructionType> instructionRepresentationDictionary;

	static {
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
