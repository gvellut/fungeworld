package com.vellut.fungeworld.mason;

import sim.portrayal.grid.FastObjectGridPortrayal2D;

// Use this to display the instruction grid
// Set color map as well (see Tutorial3, similar to trailsPortrayal)

public class FastInstructionGridPortrayal2D extends
		FastObjectGridPortrayal2D {

	@Override
	public double doubleValue(Object obj) {
		// TODO return 0..n according to instruction type
		// Group similar instruction types together (like all turn, add, +, ...)
		return super.doubleValue(obj);
	}

}
