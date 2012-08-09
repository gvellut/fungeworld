package com.vellut.fungeword.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import org.junit.Test;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.InstructionType;
import com.vellut.fungeworld.io.ProgramReader;

public class ProgramReaderTest {

	@Test
	public void testSimpleProgram() throws Exception {
		ProgramReader reader = new ProgramReader();
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("data/simple.fw");
		Instruction[][] program = reader.readProgram(is);
		assertEquals(1, program.length);

		Instruction[] line = program[0];
		assertEquals(7, line.length);

		assertEquals(InstructionType.INTEGER, line[0].getInstructionType());
		assertNotNull(line[0].getAttachedData());
		int intValue = (Integer) line[0].getAttachedData();
		assertEquals(1, intValue);

		assertEquals(InstructionType.INTEGER, line[1].getInstructionType());
		assertNotNull(line[1].getAttachedData());
		intValue = (Integer) line[1].getAttachedData();
		assertEquals(2, intValue);

		assertEquals(InstructionType.ADD, line[2].getInstructionType());
		assertNull(line[2].getAttachedData());

		assertEquals(InstructionType.INTEGER, line[3].getInstructionType());
		assertNotNull(line[3].getAttachedData());
		intValue = (Integer) line[3].getAttachedData();
		assertEquals(1, intValue);

		assertEquals(InstructionType.INTEGER, line[4].getInstructionType());
		assertNotNull(line[4].getAttachedData());
		intValue = (Integer) line[4].getAttachedData();
		assertEquals(0, intValue);

		assertEquals(InstructionType.WRITE, line[5].getInstructionType());
		assertNull(line[5].getAttachedData());

		assertEquals(InstructionType.END, line[6].getInstructionType());
		assertNull(line[6].getAttachedData());
	}

}


