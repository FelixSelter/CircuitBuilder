package com.felixselter.main;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Circuit {

	private Properties properties = new Properties();
	private File sourcefile;
	private String name;
	private Color color;
	private int inPins;
	private int outPins;
	private String[] circuits;
	private String[] endNodes;

	public Circuit(File sourcefile) {
		this.sourcefile = sourcefile;
		try {
			properties.loadFromXML(new FileInputStream(sourcefile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.name = properties.getProperty("Name");
		String[] colordata = properties.getProperty("Color").split(",");
		this.color = new Color(Integer.valueOf(colordata[0]), Integer.valueOf(colordata[1]),
				Integer.valueOf(colordata[2]), Integer.valueOf(colordata[3]));
		this.inPins = Integer.valueOf(properties.getProperty("InPins"));
		this.outPins = Integer.valueOf(properties.getProperty("OutPins"));
		this.circuits = properties.getProperty("Circuits").split(", ");
		circuits[0] = circuits[0].substring(1);
		circuits[circuits.length - 1] = circuits[circuits.length - 1].substring(0,
				circuits[circuits.length - 1].length() - 1);
		this.endNodes = properties.getProperty("EndNodes").split(", ");
		endNodes[0] = endNodes[0].substring(1);
		endNodes[endNodes.length - 1] = endNodes[endNodes.length - 1].substring(0,
				endNodes[endNodes.length - 1].length() - 1);
	}

	public Circuit(String name, Color color, int inPins, int outPins) {
		this.name = name;
		this.color = color;
		this.inPins = inPins;
		this.outPins = outPins;
	}

	public boolean[] calculate(boolean[] inValues) {
//		"(1,2)AND(6)", "(6)NOT(7)", "(3)(5)", "(7)(4)"
		HashMap<Integer, Boolean> storage = new HashMap<>();

		// setting inValues
		for (int i = 0; i < inValues.length; i++) {
			storage.put(storage.size() + 1, inValues[i]);
		}

		// trying to calculate other data

		// circuits
		ArrayList<String> circuitsLeft = new ArrayList<String>(Arrays.asList(circuits));
		int i = 0;
		while (circuitsLeft.size() > 0) {

			String[] inData = circuitsLeft.get(i).substring(1, circuitsLeft.get(i).indexOf(")")).split(",");
			boolean success = true;
			for (String id : inData) {
				if (!storage.containsKey(Integer.valueOf(id))) {
					success = false;
					break;
				}
			}

			if (success) {

				String circuitName = circuitsLeft.get(i).substring(circuitsLeft.get(i).indexOf(")") + 1,
						circuitsLeft.get(i).lastIndexOf("("));
				Circuit circuit;
				if (circuitName.equals("AND")) {
					circuit = new AndCircuit();
				} else if (circuitName.equals("NOT")) {
					circuit = new NotCircuit();
				} else {
					circuit = new Circuit(new File("Circuits" + File.separator + circuitName + ".circuit"));
				}

				boolean[] toCalculate = new boolean[inData.length];

				for (int j = 0; j < inData.length; j++) {
					toCalculate[j] = storage.get(Integer.valueOf(inData[j]));
				}
				boolean[] calculated = circuit.calculate(toCalculate);

				String[] ids = circuitsLeft.get(i).substring(circuitsLeft.get(i).lastIndexOf("(") + 1,
						circuitsLeft.get(i).indexOf(")", circuitsLeft.get(i).lastIndexOf("("))).split(",");

				for (int j = 0; j < ids.length; j++) {
					storage.put(Integer.valueOf(ids[j]), calculated[j]);
				}

				circuitsLeft.remove(circuitsLeft.get(i));
				i=0;
			}
			i++;
			if (i == circuitsLeft.size())
				i = 0;

		}

		// EndNodes
		for (String endNode : endNodes) {
			String[] data = endNode.substring(1).replace(")", "").replace("(", ",").split(",");
			storage.put(Integer.valueOf(data[1]), storage.get(Integer.valueOf(data[0])));
		}

		boolean[] toreturn = new boolean[outPins];
		for (int j = 0; j < outPins; j++) {
			toreturn[j] = storage.get(inPins + j + 1);
		}

		return toreturn;
	}

	public void save() {

		sourcefile = new File("Circuits" + File.separator + name + ".circuit");

		properties.setProperty("Name", name);
		properties.setProperty("Color",
				color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha());
		properties.setProperty("InPins", String.valueOf(inPins));
		properties.setProperty("OutPins", String.valueOf(outPins));
		properties.setProperty("Circuits", Arrays.toString(circuits));
		properties.setProperty("EndNodes", Arrays.toString(endNodes));

		try {
			properties.storeToXML(new FileOutputStream(sourcefile), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public int getInPins() {
		return inPins;
	}

	public int getOutPins() {
		return outPins;
	}

	public Circuit cloneCircuit() {
		return new Circuit(sourcefile);
	}

	public int getMaxPinsVerticaly() {
		return inPins > outPins ? inPins : outPins;
	}

	public void setCircuits(String[] circuits) {
		this.circuits = circuits;
	}

	public void setEndNodes(String[] endNodes) {
		this.endNodes = endNodes;
	}

}
