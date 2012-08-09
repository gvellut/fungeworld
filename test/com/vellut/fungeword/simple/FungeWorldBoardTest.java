package com.vellut.fungeword.simple;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.InstructionType;
import com.vellut.fungeworld.io.ProgramReader;
import com.vellut.fungeworld.simple.FungeWorldBoard;

public class FungeWorldBoardTest {

	@Test
	public void testSimpleExecution() throws Exception {
		FungeWorldBoard fw = new FungeWorldBoard(100, 100);
		ProgramReader reader = new ProgramReader();
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("data/simple.fw");
		Instruction[][] program = reader.readProgram(is);
		fw.load(program, new int[] { 0, 0 });
		
		Instruction[][] board = fw.getInstructionBoard();

		Instruction input = board[1][0];
		assertEquals(InstructionType.NOOP, input.getInstructionType());
		
		// The program simple.fw outputs a 3 at coordinates [1,0]
		fw.run(new int[] { 0, 0 }, new int[] { 0, 1 });

		Instruction output = board[1][0];
		assertEquals(InstructionType.INTEGER, output.getInstructionType());
		assertEquals(Integer.valueOf(3), (Integer) output.getAttachedData());
	}

}
