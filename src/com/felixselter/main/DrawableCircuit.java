package com.felixselter.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;

public class DrawableCircuit {

	private Circuit circuit;
	private float factorX;
	private float factorY;
	private Pin[] inPins;
	private Pin[] outPins;

	public DrawableCircuit(Circuit circuit, int x, int y, int width, int height) {
		this.circuit = circuit;
		factorX = (float) (x) / width;
		factorY = (float) (y) / height;
		inPins = new Pin[circuit.getInPins()];
		outPins = new Pin[circuit.getOutPins()];
		for (int i = 0; i < inPins.length; i++) {
			inPins[i] = new Pin(false, false);
		}
		for (int i = 0; i < outPins.length; i++) {
			outPins[i] = new Pin(false, true);
		}
		updatePins(width, height);
	}

	public void updatePins(int width, int height) {

		int x = (int) (width * factorX);
		int y = (int) (height * factorY);
		int pinDiameter = (height / 20);
		int circuitHeight = pinDiameter * circuit.getMaxPinsVerticaly();
		int circuitWidth = width / 20;

		int inPinSpace = circuitHeight / (inPins.length) - pinDiameter;
		for (int inPin = 0; inPin < circuit.getInPins(); inPin++) {
			Main.main.frame.contentpanel.circuitpanel.pins.put(inPins[inPin], new Rectangle(x - (pinDiameter / 2),
					y + (inPinSpace / 2) + (inPin) * (pinDiameter + inPinSpace), pinDiameter, pinDiameter));
		}

		int outPinSpace = circuitHeight / (outPins.length) - pinDiameter;
		for (int outPin = 0; outPin < circuit.getOutPins(); outPin++) {
			Main.main.frame.contentpanel.circuitpanel.pins.put(outPins[outPin],
					new Rectangle(x + circuitWidth - (pinDiameter / 2),
							y + (outPinSpace / 2) + (outPin) * (pinDiameter + outPinSpace), pinDiameter, pinDiameter));
		}
	}

	public void doCalculation() {// call before drawing the outer pins of the circuitpanel

		boolean[] inValues = new boolean[circuit.getInPins()];
		for (int i = 0; i < inPins.length; i++) {
			inValues[i] = inPins[i].isActive();
		}
		boolean[] outValues = circuit.calculate(inValues);
		if (outValues.length != circuit.getOutPins())
			throw (new RuntimeException("Wrong number of output values for circuit " + circuit.getName()));
		for (int i = 0; i < outPins.length; i++) {
			outPins[i].setActive(outValues[i]);
		}

	}

	public void draw(Graphics g, int width, int height) {

		Graphics2D g2 = (Graphics2D) g;
		g.setFont(new Font("", 0, width / 50));

		int x = (int) (width * factorX);
		int y = (int) (height * factorY);
		int pinDiameter = (height / 20);
		int circuitHeight = pinDiameter * circuit.getMaxPinsVerticaly();
		int circuitWidth = g.getFontMetrics().stringWidth(circuit.getName()) + 20;

		int inPinSpace = circuitHeight / (inPins.length) - pinDiameter;
		for (int inPin = 0; inPin < inPins.length; inPin++) {

			g.setColor(inPins[inPin].isActive() ? ColorManager.instance.getColorOf("ActivePin")
					: ColorManager.instance.getColorOf("Pin"));
			g.fillOval(x - (pinDiameter / 2), y + (inPinSpace / 2) + (inPin) * (pinDiameter + inPinSpace), pinDiameter,
					pinDiameter);
		}

		int outPinSpace = circuitHeight / (outPins.length) - pinDiameter;
		for (int outPin = 0; outPin < outPins.length; outPin++) {

			g.setColor(outPins[outPin].isActive() ? ColorManager.instance.getColorOf("ActivePin")
					: ColorManager.instance.getColorOf("Pin"));
			g.fillOval(x + circuitWidth - (pinDiameter / 2),
					y + (outPinSpace / 2) + (outPin) * (pinDiameter + outPinSpace), pinDiameter, pinDiameter);
		}

		g.setColor(circuit.getColor());
		g.fillRect(x, y, circuitWidth, circuitHeight);

		g.setColor(ColorManager.instance.getColorOf("Font"));
		g.drawString(circuit.getName(), x + 10, y + circuitHeight / 2 + g.getFontMetrics().getMaxAscent() / 2);

		for (Pin pin : outPins) {
			if (pin.getNextPins().size() > 0) {
				g2.setStroke(new BasicStroke(3));
				for (Pin nextPin : pin.getNextPins()) {

					HashMap<Pin, Rectangle> pins = Main.main.frame.contentpanel.circuitpanel.pins;

					g.setColor(pin.isActive() ? ColorManager.instance.getColorOf("ActivePin")
							: ColorManager.instance.getColorOf("Pin"));

					g.drawLine(pins.get(pin).x + (pins.get(pin).width / 2),
							pins.get(pin).y + (pins.get(pin).height / 2),
							pins.get(nextPin).x + pins.get(nextPin).width / 2,
							pins.get(nextPin).y + pins.get(nextPin).height / 2);
				}
			}
		}

	}

	public Circuit getCircuit() {
		return circuit;
	}

	public Pin[] getInPins() {
		return inPins;
	}

	public Pin[] getOutPins() {
		return outPins;
	}

}
