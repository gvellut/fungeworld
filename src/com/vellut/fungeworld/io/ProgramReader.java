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

	public Instruction[][] readProgram(InputStream is) throws IOException, ProgramReaderException {
		List<String> lines = IOUtils.readLines(is, "UTF-8");
		Instruction[][] program = new Instruction[lines.size()][];
		// Instruction noop = new Instruction(InstructionType.NOOP);
		
		for(int currentLineIndex = 0 ; currentLineIndex < lines.size() ; currentLineIndex++) {
			String line = lines.get(currentLineIndex).trim();
			if (line.isEmpty()) {
				// No instruction on line
				program[currentLineIndex] = new Instruction[0];
				continue;
			}

			String[] words = line.split(";");
			program[currentLineIndex] = new Instruction[words.length];
			
			for(int currentWordIndex = 0 ; currentWordIndex < words.length ; currentWordIndex++) {
				String word = words[currentWordIndex];
				
				// First char of instruction is sufficient to determine its type 
				String char1 = word.substring(0, 1);
				InstructionType iType = stringToInstructionTypeMap.get(char1);
				if(iType == null) {
					throw new ProgramReaderException("Unknown instruction: " + word);
				}
				
				Instruction instr = new Instruction(iType);
				
				if(iType == InstructionType.INTEGER) {
					// Integer has attached data: After 1st character
					String intData = word.substring(1);
					instr.setAttachedData(Integer.valueOf(intData));
				}
				
				program[currentLineIndex][currentWordIndex] = instr;
				
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
