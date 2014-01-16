package com.vellut.fungeword.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import org.junit.Test;

import com.vellut.fungeworld.io.ProgramReader;
import com.vellut.fungeworld.lang.Instruction;
import com.vellut.fungeworld.lang.InstructionType;

public class ProgramReaderTest {

	@Test
	public void testSimpleProgram() throws Exception {
		ProgramReader reader = new ProgramReader();
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("data/simple.fw");
		Instruction[][] program = reader.readProgram(is);
		assertEquals(7, program.length);

		Instruction[] column = program[0];
		assertEquals(1, column.length);

		assertEquals(InstructionType.INTEGER,
				program[0][0].getInstructionType());
		assertNotNull(program[0][0].getAttachedData());
		int intValue = (Integer) program[0][0].getAttachedData();
		assertEquals(1, intValue);

		assertEquals(InstructionType.INTEGER,
				program[1][0].getInstructionType());
		assertNotNull(program[1][0].getAttachedData());
		intValue = (Integer) program[1][0].getAttachedData();
		assertEquals(2, intValue);

		assertEquals(InstructionType.ADD, program[2][0].getInstructionType());
		assertNull(program[2][0].getAttachedData());

		assertEquals(InstructionType.INTEGER,
				program[3][0].getInstructionType());
		assertNotNull(program[3][0].getAttachedData());
		intValue = (Integer) program[3][0].getAttachedData();
		assertEquals(0, intValue);

		assertEquals(InstructionType.INTEGER,
				program[4][0].getInstructionType());
		assertNotNull(program[4][0].getAttachedData());
		intValue = (Integer) program[4][0].getAttachedData();
		assertEquals(1, intValue);

		assertEquals(InstructionType.WRITE, program[5][0].getInstructionType());
		assertNull(program[5][0].getAttachedData());

		assertEquals(InstructionType.END, program[6][0].getInstructionType());
		assertNull(program[6][0].getAttachedData());
	}

}


