package com.vellut.fungeworld.mason;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import com.vellut.fungeworld.Instruction;
import com.vellut.fungeworld.InstructionType;

public class Simulation extends SimState {

	public ObjectGrid2D instructionGrid;
	public SparseGrid2D processGrid;

	public int gridWidth;
	public int gridHeight;
	public int numProcesses;

	public Simulation(long seed, int gridWidth, int gridHeight, int numProcesses) {
		super(seed);
		
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.numProcesses = numProcesses;
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
				Instruction instruction = randomInstruction();
				instructionGrid.field[i][j] = instruction;
			}
		}
	}

	// FIXME make something better
	private Instruction randomInstruction() {
		InstructionType[] instructionTypes = InstructionType.values();
		int index = random.nextInt(instructionTypes.length);
		InstructionType instructionType = instructionTypes[index];

		if (instructionType == InstructionType.SPAWN) {
			// replace 80% with dup so not spawned too often
			if (random.nextInt(10) < 8) {
				instructionType = InstructionType.NOOP;
			}
		}

		Instruction instruction = new Instruction(instructionType);

		// Only integers can have attached data
		if (instructionType == InstructionType.INTEGER) {
			int intValue = random.nextInt(gridWidth);
			instruction.setAttachedData(intValue);
		}

		return instruction;
	}

	private void initProcessGrid() {
		BoardIO boardIO = new BoardIO(1, 6, 10);
		for (int i = 0; i < numProcesses; i++) {
			int x = random.nextInt(gridWidth);
			int y = random.nextInt(gridHeight);
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
	
	public static void main(String[] args) {
		Simulation sim = new Simulation(System.currentTimeMillis(), 200, 200,
				50);
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

