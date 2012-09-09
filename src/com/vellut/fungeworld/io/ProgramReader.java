package com.vellut.fungeworld.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.InstructionType;

public class ProgramReader {
	
	private Map<String, InstructionType> stringToInstructionTypeMap;
	
	public ProgramReader() {
		this.stringToInstructionTypeMap = new HashMap<String, InstructionType>();
		buildStringToInstructionMap();
	}

	// Returns a rectangular grid (filled with NOOP if line is not full)
	public Instruction[][] readProgram(InputStream is) throws IOException, ProgramReaderException {
		List<String> lines = IOUtils.readLines(is, "UTF-8");
		int numLines = lines.size();
		int numColumns = 0;
		for (String line : lines) {
			if (line.length() > numColumns) {
				numColumns = line.length();
			}
		}

		Instruction[][] program = new Instruction[numColumns][numLines];
		Instruction noop = new Instruction(InstructionType.NOOP);
		
		for (int currentLineIndex = 0; currentLineIndex < numLines; currentLineIndex++) {
			String line = lines.get(currentLineIndex).trim();

			int currentWordIndex = 0;
			if (!line.isEmpty()) {
				String[] words = line.split(";");
				for (currentWordIndex = 0; currentWordIndex < words.length; currentWordIndex++) {
					String word = words[currentWordIndex];

					// First char of instruction is sufficient to determine its
					// type
					String char1 = word.substring(0, 1);
					InstructionType iType = stringToInstructionTypeMap
							.get(char1);
					if (iType == null) {
						throw new ProgramReaderException(
								"Unknown instruction: " + word);
					}

					Instruction instr = new Instruction(iType);

					if (iType == InstructionType.INTEGER) {
						// Integer has attached data: After 1st character
						String intData = word.substring(1);
						instr.setAttachedData(Integer.valueOf(intData));
					}

					program[currentWordIndex][currentLineIndex] = instr;
				}
			}
			
			// fill the rest with NOOP
			for (int restLine = currentWordIndex; restLine < numColumns; restLine++) {
				program[restLine][currentLineIndex] = noop;
			}
		}
		
		return program;
	}
	
	public void buildStringToInstructionMap() {
		for(InstructionType iType : InstructionType.values()) {
			stringToInstructionTypeMap.put(iType.getRepresentation(), iType);
		}
	}
	
}
