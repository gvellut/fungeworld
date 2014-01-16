package com.vellut.fungeworld.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.vellut.fungeworld.lang.Instruction;

public class ProgramWriter {

	// If an instruction to output is < minLength, space is added at the end
	// so its length becomes minLength
	public void writeProgram(OutputStream os, Instruction[][] program,
			int minLength)
			throws IOException {
		Writer out = new BufferedWriter(new OutputStreamWriter(os));
		
		// we must go through the lines first
		for (int j = 0; j < program[0].length; j++) {
			for (int i = 0; i < program.length; i++) {
				Instruction instr = program[i][j];
				if (i != 0) {
					try {
						// Separator between words
						out.write(";");
					} catch (UnsupportedEncodingException ex) {
						// Never happen
					}
				}
				
				String strInstr = instr.toString();
				out.write(strInstr);
				for (int k = strInstr.length(); k < minLength; k++) {
					out.write(" ");
				}

			}
			
			out.write("\r\n");
		}
	}
	
	public void writeProgram(OutputStream os, Instruction[][] program)
			throws IOException {
		// no padding
		writeProgram(os, program, 0);
	}

}
