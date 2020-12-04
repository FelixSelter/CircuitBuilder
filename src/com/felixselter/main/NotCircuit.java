package com.felixselter.main;

public class NotCircuit extends Circuit {

	public NotCircuit() {
		super("NOT", ColorManager.instance.getColorOf("NotCircuit"), 1, 1);
	}

	@Override
	public boolean[] calculate(boolean[] inValues) {
		if (inValues.length == getInPins()) {
			return new boolean[] { !inValues[0] };
		} else {
			throw (new RuntimeException("To many InputValues for this circuit: " + getName()));
		}
	}

	@Override
	public Circuit cloneCircuit() {
		return new NotCircuit();
	}
}
