package com.vellut.fungeworld.mason;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import com.vellut.fungeworld.Utils;
import com.vellut.fungeworld.io.InstructionGridFiller;
import com.vellut.fungeworld.io.NoopFiller;
import com.vellut.fungeworld.io.ProgramReader;
import com.vellut.fungeworld.io.ProgramReaderException;
import com.vellut.fungeworld.lang.Instruction;

public class Simulation extends SimState {

	private BoardIO boardIO;
	private InstructionGridFiller filler;

	public ObjectGrid2D instructionGrid;
	public SparseGrid2D processGrid;

	public int gridWidth;
	public int gridHeight;
	public int numProcesses;

	public Simulation(long seed, int gridWidth, int gridHeight, int numProcesses, BoardIO boardIO) {
		super(seed);
		
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.numProcesses = numProcesses;
		this.boardIO = boardIO;
	}

	@Override
	public void start() {
		super.start();
		instructionGrid = new ObjectGrid2D(gridWidth, gridHeight);
		processGrid = new SparseGrid2D(gridWidth, gridHeight);

		// fill the instruction Grid with random instructions
		initInstructionGrid();

		// create a few processes on the grid
		initProcessGrid();

	}

	private void initInstructionGrid() {
		// FIXME offer a better mix of instructions
		// FIXME precreate all instructions and use one object instance
		// for each type (except integer)
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				Instruction instruction = Utils.randomInstruction(random, gridWidth);
				instructionGrid.field[i][j] = instruction;
			}
		}
	}

	// FIXME make something better
	

	private void copyAncestorToXY(int x, int y) {
		InputStream is = null;
		try {
			ProgramReader reader = new ProgramReader(filler);
			is = this.getClass().getClassLoader()
					.getResourceAsStream("data/ancestor.fw");
			Instruction[][] program = reader.readProgram(is);
			for (int i = 0; i < program.length; i++) {
				for (int j = 0; j < program[i].length; j++) {
					instructionGrid.field[instructionGrid.tx(x + i)][instructionGrid
							.ty(y + j)] = program[i][j];
				}
			}
		} catch (IOException | ProgramReaderException ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private void initProcessGrid() {
		for (int i = 0; i < numProcesses; i++) {
			int x = random.nextInt(gridWidth);
			int y = random.nextInt(gridHeight);

			// First, copy ancestor program at x y
			copyAncestorToXY(x, y);

			// Then create Process starting on x y
			Process process = new Process(x, y, 1, 0, boardIO);

			// Location info is duplicated (in the interpreter and in the
			// Grid)
			processGrid.setObjectLocation(process, new Int2D(x, y));
			schedule.scheduleOnce(Schedule.EPOCH, process);
		}

	}
	
	@Override
	public void finish() {
		super.finish();
		
	}

	public void setFiller(InstructionGridFiller filler) {
		this.filler = filler;
	}
	
	public static void main(String[] args) {
		BoardIO boardIO = new BoardIO(new NoMutationStrategy(), 0, 0);
		Simulation sim = new Simulation(System.currentTimeMillis(), 200, 200,
				50, boardIO);
		sim.start();
		long steps;
		do {
			if (!sim.schedule.step(sim))
				break;
			steps = sim.schedule.getSteps();
			if (steps % 500 == 0)
				System.out.println("Steps: " + steps + " Time: "
						+ sim.schedule.getTime());
		} while (steps < 5000);

		sim.finish();
		System.exit(0);
    }
}

