package com.felixselter.main;

import java.awt.Color;

public class AndCircuit extends Circuit {

	public AndCircuit() {
		super("AND", ColorManager.instance.getColorOf("AndCircuit"), 2, 1);
	}

	@Override
	public boolean[] calculate(boolean[] inValues) {
		if (inValues.length == getInPins()) {
			return new boolean[] { inValues[0] && inValues[1] };
		} else {
			throw (new RuntimeException("To many InputValues for this circuit: " + getName()));
		}
	}

	@Override
	public Circuit cloneCircuit() {
		return new AndCircuit();
	}
}
