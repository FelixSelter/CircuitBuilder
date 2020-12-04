package com.felixselter.main;

import java.util.ArrayList;
import java.util.Arrays;

public class Pin {

	private boolean active = false;
	private boolean inputMode;
	private ArrayList<Pin> nextPins = new ArrayList<>();
	private Pin previousPin;
	private boolean expandable;

	public Pin(boolean inputMode, boolean expandable) {
		this.inputMode = inputMode;
		this.expandable = expandable;
	}

	public boolean isInputMode() {
		return inputMode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		for (Pin pin : nextPins) {
			pin.setActive(active);
		}
	}

	public ArrayList<Pin> getNextPins() {
		return nextPins;
	}

	public void addNextPin(Pin nextPin) {
		if (nextPin.previousPin == null)
			nextPin.connect(this);

	}

	public void connect(Pin previousPin) {
		this.previousPin = previousPin;
		previousPin.nextPins.add(this);
	}
	
	public Pin getPreviousPin() {
		return previousPin;
	}

	public boolean isExpandable() {
		return expandable;
	}
	public boolean hasPreviousPin() {
		return previousPin!=null;
	}

}
