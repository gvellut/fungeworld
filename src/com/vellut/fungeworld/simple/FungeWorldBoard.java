package com.vellut.fungeworld.simple;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.vellut.fungeworld.io.ProgramReader;
import com.vellut.fungeworld.io.ProgramReaderException;
import com.vellut.fungeworld.io.ProgramWriter;
import com.vellut.fungeworld.lang.BoardProxy;
import com.vellut.fungeworld.lang.Instruction;
import com.vellut.fungeworld.lang.InstructionType;
import com.vellut.fungeworld.lang.Interpreter;
import com.vellut.fungeworld.lang.InterpreterException;
import com.vellut.fungeworld.lang.InterpreterState;

public class FungeWorldBoard implements BoardProxy {

	Instruction[][] board;
	int width, height;
	Interpreter interpreter;

	// Same order as Mason
	public FungeWorldBoard(int width, int height) {
		this.width = width;
		this.height = height;
		board = new Instruction[width][height];
		interpreter = new Interpreter(2, this);
		interpreter.captureOutput(true);
		clearBoard();
	}


	// Fills the board with NOOP
	private void clearBoard() {
		Instruction noop = new Instruction(InstructionType.NOOP);
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = noop;
			}
		}
	}

	public List<Instruction> getOutput() {
		return interpreter.getOutput();
	}

	// Load a rectangle of instructions at a certain coordinate
	public void load(Instruction[][] program, int[] offset) {
		// Assume size of board is sufficient
		for (int i = 0; i < program.length; i++) {
			for (int j = 0; j < program[i].length; j++) {
				board[offset[0] + i][offset[1] + j] = program[i][j];
			}
		}
	}

	public void run(int[] instructionPointer, int[] deltaInstructionPointer) {

		try {
			interpreter.setInitialState(instructionPointer,
					deltaInstructionPointer);
		} catch (InterpreterException e) {
			System.out.println("Bad initialization parameters");
			return;
		}

		while (interpreter.getState() != InterpreterState.KILLED) {
			try {
				int[] currentIP = interpreter.getInstructionPointer();
				if (!isIndexValid(currentIP)) {
					correctIndex(currentIP);
				}
				Instruction instr = getData(currentIP);
				interpreter.executeInstruction(instr);
			} catch (InterpreterException e) {
				System.out.println(e.getMessage());
				// FIXME output board for checking
				return;
			}
		}
	}

	private Instruction getData(int[] index) {
		return board[index[0]][index[1]];
	}

	private boolean isIndexValid(int[] index) {
		return (index[0] < width && index[1] < height);
	}

	private void correctIndex(int[] index) {
		index[0] = index[0] % width;
		index[1] = index[1] % height;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	public String toString(int minLength) {
		ProgramWriter pWriter = new ProgramWriter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			pWriter.writeProgram(baos, board, minLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// FIXME output state of Interpreter
		String output = null;
		try {
			output = baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Never happen
		}
		
		return output;
	}

	@Override
	public void read(int[] index) {
		try {
			// Just read the board
			if (!isIndexValid(index)) {
				correctIndex(index);
			}
			interpreter.onReadResponse(getData(index));
		} catch (InterpreterException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void write(int[] index, Instruction value) {
		if (!isIndexValid(index)) {
			correctIndex(index);
		}
		board[index[0]][index[1]] = value;
	}
	
	@Override
	public void mutate(int[] index, Instruction value) {
		if (!isIndexValid(index)) {
			correctIndex(index);
		}
		
		// Infinite spawns => we replace by another spawn
		if(value.getInstructionType() == InstructionType.SPAWN) {
			// reset counter to big value
			value.setAttachedData(100);
		}
	}

	@Override
	public void waitUntilWrite(int[] memoryCell) {
		// Will never be called (only one interpreter on this board)
	}

	@Override
	public void spawn(int[] memoryCell, int[] delta) {
		// Will never be called (only one interpreter on this board)
	}

	public Instruction[][] getInstructionBoard() {
		return board;
	}

	public static void main(String args[]) {
		FileInputStream fis = null;
		try {
			FungeWorldBoard board = new FungeWorldBoard(100, 100);
			ProgramReader reader = new ProgramReader();
			fis = new FileInputStream(args[0]);
			Instruction[][] program = reader.readProgram(fis);
			board.load(program, new int[]{0,0});
			board.run(new int[] { 0, 0 }, new int[] { 1, 0 });
			System.out.println(board.toString());
		} catch (IOException | ProgramReaderException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
		}

	}

}
