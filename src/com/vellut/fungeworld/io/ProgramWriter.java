package com.vellut.fungeworld.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.vellut.fungeworld.Instruction;

public class ProgramWriter {

	public void writeProgram(OutputStream os, Instruction[][] program) throws IOException {
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
				
				out.write(instr.toString());
			}
			
			out.write("\r\n");
		}
	}
	
}
