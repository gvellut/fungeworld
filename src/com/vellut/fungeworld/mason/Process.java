package com.vellut.fungeworld.mason;

import java.util.Stack;

import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

import com.vellut.fungeworld.lang.BoardProxy;
import com.vellut.fungeworld.lang.Instruction;
import com.vellut.fungeworld.lang.InstructionType;
import com.vellut.fungeworld.lang.Interpreter;
import com.vellut.fungeworld.lang.InterpreterException;
import com.vellut.fungeworld.lang.InterpreterState;

public class Process implements Steppable, BoardProxy {
	private final static Logger log = Logger.getLogger(Process.class);

	private static int counter = 0;

	private Interpreter interpreter;
	// Need this to access the instructionGrid in the Read method
	private Simulation sim;
	private int id;
	private BoardIO boardIO;

	public Process(int x, int y, int deltaX, int deltaY, BoardIO boardIO) {
		interpreter = new Interpreter(2, this);
		this.boardIO = boardIO;
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

		try {
			interpreter.executeInstruction(instruction);
		} catch (InterpreterException e) {
		
			e.printStackTrace();
		}

		if (interpreter.getState() == InterpreterState.KILLED) {
			System.out.println("Killed " + id + " " + sim.schedule.getSteps());
			sim.processGrid.remove(this);
		} else {
			int[] ip = interpreter.getInstructionPointer();
			if (!isIndexValid(ip)) {
				correctIndex(ip);
			}

			// Move (sync with internal position of interpreter)
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
		Instruction instruction = boardIO.read(sim, memoryCell);
		try {
			interpreter.onReadResponse(instruction);
		} catch (InterpreterException e) {
			log.error("Error reading cell", e);
		}
	}

	@Override
	public void write(int[] memoryCell, Instruction value) {
		if (!isIndexValid(memoryCell)) {
			correctIndex(memoryCell);
		}
		boardIO.write(sim, memoryCell, value);
	}

	@Override
	public void waitUntilWrite(int[] memoryCell) {
		// FIXME implement when the instruction is added to interpreter
	}

	@Override
	public void spawn(int[] memoryCell, int[] delta) {
		if (!isIndexValid(memoryCell)) {
			correctIndex(memoryCell);
		}
		System.out.println("Spawning");
		Process childProcess = new Process(memoryCell[0], memoryCell[1],
				delta[0], delta[1], boardIO);
		sim.processGrid.setObjectLocation(childProcess,
				memoryCell[0], memoryCell[1]);
		sim.schedule.scheduleOnceIn(1, childProcess);
	}
	
	@Override
	public void mutate(int[] memoryCell, Instruction value) {
		if (!isIndexValid(memoryCell)) {
			correctIndex(memoryCell);
		}
		
		// Infinite spawns here
		if(value.getInstructionType() == InstructionType.SPAWN) {
			// FIXME extract arbitrary value
			boardIO.writeRandom(sim,  memoryCell, 40);
		}
	}

	// Properties for MASON Inspector

	public int getId() {
		return id;
	}

	// TODO implement setters (make sure the position is also
	// updated on grid)
	public int getInstructionPointerX() {
		return interpreter.getInstructionPointer()[0];
	}

	public int getInstructionPointerY() {
		return interpreter.getInstructionPointer()[1];
	}

	public int getDeltaInstructionPointerX() {
		return interpreter.getDeltaInstructionPointer()[0];
	}

	public int getDeltaInstructionPointerY() {
		return interpreter.getDeltaInstructionPointer()[1];
	}

	// TODO create MASON inspector for the full stack
	public String getTopInstruction() {
		Stack<Instruction> execStack = interpreter.getExecutionStack();
		if (!execStack.isEmpty()) {
			Instruction instruction = execStack.peek();
			if (instruction != null) {
				return instruction.toString();
			}
		}
		return null;
	}

}
