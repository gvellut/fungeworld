package com.vellut.fungeworld.mason;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.Interpreter;
import com.vellut.fungeworld.InterpreterException;
import com.vellut.fungeworld.InterpreterState;
import com.vellut.fungeworld.MemoryReaderWriter;

public class Process implements Steppable, MemoryReaderWriter {

	private static int counter = 0;

	private Interpreter interpreter;
	// Need this to access the instructionGrid in the Read method
	private Simulation sim;
	private int id;

	public Process(int x, int y, int deltaX, int deltaY) {
		interpreter = new Interpreter(2, this);
		try {
			interpreter.setInitialState(new int[] { x, y }, new int[] { deltaX,
					deltaY });
		} catch (InterpreterException e) {
			// Should never happen
		}
		id = counter++;
	}

	@Override
	public void step(SimState state) {
		sim = (Simulation) state;
		Int2D position = sim.processGrid.getObjectLocation(this);
		Instruction instruction = (Instruction) sim.instructionGrid.field[position.x][position.y];
		if (id < 1) {
			System.out.println(id + " " + instruction.getInstructionType());
		}
		try {
			interpreter.executeInstruction(instruction);
		} catch (InterpreterException e) {
			// FIXME log to a file
			e.printStackTrace();
		}

		if (interpreter.getState() == InterpreterState.KILLED) {
			System.out.println("Killed " + id + " " + sim.schedule.getSteps());
			sim.processGrid.remove(this);
		} else {
			// Always correct the IP of the interpreter
			int[] ip = interpreter.getInstructionPointer();
			if (!isIndexValid(ip)) {
				correctIndex(ip);
			}
			if (id < 1) {
				System.out.println(id + " X " + ip[0] + " Y " + ip[1]);
			}
			// Move
			sim.processGrid.setObjectLocation(this, ip[0], ip[1]);
			sim.schedule.scheduleOnceIn(1, this);
		}
	}

	private boolean isIndexValid(int[] index) {
		return (index[0] >= 0 && index[0] < sim.instructionGrid.getWidth()
				&& index[1] >= 0 && index[1] < sim.instructionGrid.getHeight());
	}

	// Wrap the indices
	private void correctIndex(int[] index) {
		index[0] = sim.processGrid.tx(index[0]);
		index[1] = sim.processGrid.ty(index[1]);
	}

	@Override
	public void read(int[] memoryCell) {
		if (!isIndexValid(memoryCell)) {
			correctIndex(memoryCell);
		}
		// Here, the read is synchronous
		Instruction instruction = (Instruction) sim.instructionGrid.field[memoryCell[0]][memoryCell[1]];
		try {
			interpreter.onReadResponse(instruction);
		} catch (InterpreterException e) {
			// FIXME log to a file
			e.printStackTrace();
		}
	}

	@Override
	public void write(int[] memoryCell, Instruction value) {
		if (!isIndexValid(memoryCell)) {
			correctIndex(memoryCell);
		}
		sim.instructionGrid.field[memoryCell[0]][memoryCell[1]] = value;
	}

	@Override
	public void waitUntilWrite(int[] memoryCell) {
		// FIXME implement when the instruction is added to interpreter
	}

}
