package com.vellut.fungeworld.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.InstructionType;

public class ProgramWriter {

	public void writeProgram(OutputStream os, Instruction[][] program) throws IOException {
		Writer out = new BufferedWriter(new OutputStreamWriter(os));
		
		for(int i = 0 ; i < program.length ; i++) {
			Instruction[] programLine = program[i];
			for(int j = 0 ; j < programLine.length ; j++) {
				Instruction instr = programLine[j];
				if(j != 0) {
					try {
						// Separator between words
						out.write(";");
					} catch (UnsupportedEncodingException ex) {
						// Never happen
					}
				}
				
				InstructionType iType = instr.getInstructionType();
				out.write(iType.getRepresentation());
				
				if(iType == InstructionType.INTEGER) {
					int data = (Integer)instr.getAttachedData();
					out.write(Integer.toString(data));
				}
			}
			
			out.write("\r\n");
		}
	}
	
}
