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
		
		// First pass : get the words
		String[][] parsedLines = new String[numLines][];
		for (int currentLineIndex = 0; currentLineIndex < numLines; currentLineIndex++) {
			String line = lines.get(currentLineIndex).trim();
			if (!line.isEmpty()) {
				String[] words = line.split(";");
				if (words.length > numColumns) {
					numColumns = words.length;
				}
				parsedLines[currentLineIndex] = words;
			} else {
				parsedLines[currentLineIndex] = new String[0];
			}

		}

		// Second pass : get the whole program in a rectangular grid
		Instruction[][] program = new Instruction[numColumns][numLines];
		Instruction noop = new Instruction(InstructionType.NOOP);

		// Go through the columns in a line first
		for (int j = 0; j < numLines; j++) {
			int i;
			for (i = 0; i < parsedLines[j].length; i++) {
				String word = parsedLines[j][i];

				// First char of instruction is sufficient to determine its
				// type
				String char1 = word.substring(0, 1);
				InstructionType iType = stringToInstructionTypeMap.get(char1);
				if (iType == null) {
					throw new ProgramReaderException("Unknown instruction: "
							+ word);
				}

				Instruction instr = new Instruction(iType);

				if (iType == InstructionType.INTEGER) {
					// Integer has attached data: After 1st character
					String intData = word.substring(1).trim();
					instr.setAttachedData(Integer.valueOf(intData));
				}

				program[i][j] = instr;
			}

			// Fill the rest with NOOP (so the whole rectangle is filled
			// with instructions)
			for (int restLine = i; restLine < numColumns; restLine++) {
				program[restLine][j] = noop;
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
