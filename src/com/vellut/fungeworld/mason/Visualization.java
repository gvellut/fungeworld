package com.vellut.fungeworld.mason;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.gui.SimpleColorMap;

public class Visualization extends GUIState {

	public Display2D display;
	public JFrame displayFrame;

	SparseGridPortrayal2D processGridPortrayal = new SparseGridPortrayal2D();
	FastInstructionGridPortrayal2D instructionGridPortrayal = new FastInstructionGridPortrayal2D();

	public Visualization(long seed, int gridWidth, int gridHeight,
			int numProcesses) {
		super(new Simulation(seed, gridWidth, gridHeight, numProcesses));
	}

	public Visualization(SimState state) {
		super(state);
	}

	public static String getName() {
		return "FungeWorld Visualization";
	}

	public static Object getInfo() {
		return "<h2>FungeWorld</h2><p>Simple FungeWorld process and instruction visualization";
	}

	@Override
	public void quit() {
		super.quit();

		if (displayFrame != null)
			displayFrame.dispose();

		displayFrame = null;
		display = null;
	}

	@Override
	public void start() {
		super.start();
		setupPortrayals();
	}

	@Override
	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	public void setupPortrayals() {
		Simulation sim = (Simulation) state;

		processGridPortrayal.setField(sim.processGrid);
		processGridPortrayal
				.setPortrayalForAll(new OvalPortrayal2D(Color.CYAN));

		instructionGridPortrayal.setField(sim.instructionGrid);
		// TODO use color generation instead ?
		Color[] colors = new Color[12];
		colors[0] = Color.LIGHT_GRAY;
		colors[1] = Color.BLACK;
		colors[2] = Color.GREEN;
		colors[3] = Color.ORANGE;
		colors[4] = Color.YELLOW;
		colors[5] = Color.PINK;
		colors[6] = Color.BLUE;
		colors[7] = Color.GRAY;
		colors[8] = Color.RED;
		colors[9] = Color.WHITE;
		colors[10] = new Color(255, 128, 64);
		colors[11] = new Color(128, 164, 200);

		instructionGridPortrayal.setMap(new SimpleColorMap(colors));

		display.reset();
		display.repaint();
	}

	@Override
	public void init(Controller c) {
		super.init(c);

		display = new Display2D(600, 600, this);
		displayFrame = display.createFrame();
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.setBackdrop(Color.black);

		display.attach(instructionGridPortrayal, "Instructions");
		display.attach(processGridPortrayal, "Processes");
     }


	public static void main(String[] args) {
		Visualization viz = new Visualization(System.nanoTime(), 300, 300, 150);
		viz.createController();
	}

}
