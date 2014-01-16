package com.vellut.fungeword.simple;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vellut.fungeworld.io.ProgramReader;
import com.vellut.fungeworld.lang.Instruction;
import com.vellut.fungeworld.lang.InstructionType;
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

		Instruction input = board[0][1];
		assertEquals(InstructionType.NOOP, input.getInstructionType());
		
		// The program simple.fw outputs a 3 at coordinates [0,1]
		fw.run(new int[] { 0, 0 }, new int[] { 1, 0 });

		Instruction output = board[0][1];
		assertEquals(InstructionType.INTEGER, output.getInstructionType());
		assertEquals(Integer.valueOf(3), (Integer) output.getAttachedData());
	}

	@Test
	public void testTroll1Execution() throws Exception {
		FungeWorldBoard fw = new FungeWorldBoard(100, 100);
		ProgramReader reader = new ProgramReader();
		// The program outputs itself
		// see http://www.nyx.net/~gthompso/self_bf.txt
		// by Andrew Turley
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("data/troll1.fw");
		Instruction[][] program = reader.readProgram(is);
		fw.load(program, new int[] { 0, 0 });

		fw.run(new int[] { 0, 0 }, new int[] { 1, 0 });

		List<Instruction> response = fw.getOutput();

		// What the output should be : The source of the program output
		// with i10 between each line
		List<Instruction> shouldBe = new ArrayList<>();
		// Line by line
		for (int j = 0; j < program[0].length; j++) {
			for (int i = 0; i < program.length; i++) {
				shouldBe.add(program[i][j]);
			}
			// The original troll does I/O slightly differently
			// and outputs the character with ASCII value 10 (newline) between
			// each line. However we don't do any conversion to/from ASCII
			Instruction instr10 = new Instruction(InstructionType.INTEGER);
			instr10.setAttachedData(10);
			shouldBe.add(instr10);
		}

		assertEquals(shouldBe.size(), response.size());
		for (int i = 0; i < response.size(); i++) {
			assertEquals(shouldBe.get(i), response.get(i));
		}

	}

	@Test
	public void testTroll2Execution() throws Exception {
		FungeWorldBoard fw = new FungeWorldBoard(100, 100);
		ProgramReader reader = new ProgramReader();
		// The program copies itself
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("data/troll2.fw");
		Instruction[][] program = reader.readProgram(is);
		fw.load(program, new int[] { 0, 0 });

		fw.run(new int[] { 0, 0 }, new int[] { 1, 0 });

		Instruction[][] board = fw.getInstructionBoard();
		// check that copy (which starts at 20) is identical to program
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 4; j++) {
				assertEquals(program[i][j], board[i + 20][j]);
			}
		}

	}

}
