package com.vellut.fungeworld.simple;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.InstructionType;
import com.vellut.fungeworld.Interpreter;
import com.vellut.fungeworld.InterpreterException;
import com.vellut.fungeworld.InterpreterState;
import com.vellut.fungeworld.MemoryReaderWriter;
import com.vellut.fungeworld.io.ProgramReader;
import com.vellut.fungeworld.io.ProgramReaderException;
import com.vellut.fungeworld.io.ProgramWriter;

public class FungeWorldBoard implements MemoryReaderWriter {

	Instruction[][] board;
	int width, height;
	Interpreter interpreter;

	// Same order as Mason
	public FungeWorldBoard(int width, int height) {
		this.width = width;
		this.height = height;
		board = new Instruction[width][height];
		interpreter = new Interpreter(2, this);
		clearBoard();
	}

	// Fills the board with NOOP
	private void clearBoard() {
		Instruction noop = new Instruction(InstructionType.NOOP);
		for (int i = 0; i < board.length; i++) {
			Instruction[] boardLine = board[i];
			for (int j = 0; j < boardLine.length; j++) {
				boardLine[j] = noop;
			}
		}
	}

	// Load a rectangle of instructions at a certain coordinate
	public void load(Instruction[][] program, int[] offset) {
		// Assume size of board is sufficient
		for(int i = 0; i < program.length; i++) {
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
		ProgramWriter pWriter = new ProgramWriter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			pWriter.writeProgram(baos, board);
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
	public void waitUntilWrite(int[] memoryCell) {
		// Will never be called (only one interpreter on this board)
	}

	public Instruction[][] getInstructionBoard() {
		return board;
	}

	public static void main(String args[]) {
		try {
			FungeWorldBoard board = new FungeWorldBoard(100, 100);
			ProgramReader reader = new ProgramReader();
			FileInputStream fis = new FileInputStream(args[0]);
			Instruction[][] program = reader.readProgram(fis);
			board.load(program, new int[]{0,0});
			board.run(new int[]{0, 0}, new int[]{0,1});
			System.out.println(board.toString());
		} catch (IOException | ProgramReaderException e) {
			e.printStackTrace();
		}

	}

}
