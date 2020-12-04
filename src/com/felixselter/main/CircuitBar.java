package com.felixselter.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class CircuitBar extends JPanel {

	private JButton create = new JButton("CREATE");

	public CircuitBar() {
		setBackground(new Color(30, 30, 30));
		setLayout(new GridLayout());
		setBorder(new LineBorder(getBackground(), 5));
		((GridLayout) getLayout()).setHgap(10);
		((GridLayout) getLayout()).setVgap(10);
		add(create);
		add(new CircuitButton(new AndCircuit()));
		add(new CircuitButton(new NotCircuit()));
		for (String filename : new File("Circuits").list()) {
			add(new CircuitButton(new Circuit(new File("Circuits" + File.separator + filename))));
		}

		create.setLayout(new BorderLayout());
		create.setBackground(new Color(59, 59, 59));
		create.setForeground(new Color(213, 213, 213));
		create.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				create.setFont(new Font("", 0, create.getHeight() / 2));
			}
		});
		create.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				CircuitPanel panel = Main.main.frame.contentpanel.circuitpanel;

				Circuit circuit = new Circuit(Main.main.frame.contentpanel.textfield.getText(),
						new Color((int) (Math.random() * 0x1000000)), panel.pinsL.size(), panel.pinsR.size());

				ArrayList<String[]> data = generateData();
				circuit.setCircuits(data.get(0));
				circuit.setEndNodes(data.get(1));
				circuit.save();

				add(new CircuitButton(circuit));
				panel.circuits.clear();
				panel.pinsL.clear();
				panel.pinsR.clear();
				panel.pins.clear();
				Main.main.frame.repaint();
				Main.main.frame.revalidate();

			}
		});
	}

	private ArrayList<String[]> generateData() {

		ArrayList<String> circuits = new ArrayList<>();
		CircuitPanel panel = Main.main.frame.contentpanel.circuitpanel;
		HashMap<Pin, Integer> pins = new HashMap<>();
		for (Pin pin : panel.pinsL)
			pins.put(pin, pins.size() + 1);
		for (Pin pin : panel.pinsR)
			pins.put(pin, pins.size() + 1);

		for (DrawableCircuit drawableCircuit : panel.circuits) {
			Circuit circuit = drawableCircuit.getCircuit();
			String datapart = "";

			for (Pin inPin : drawableCircuit.getInPins()) {
				if (inPin.hasPreviousPin()) {

					Pin previousPin = inPin.getPreviousPin();
					if (!pins.containsKey(previousPin))
						pins.put(previousPin, pins.size() + 1);
					datapart += "," + String.valueOf(pins.get(previousPin));
				} else {
					datapart += ",0";
				}
			}

			datapart += ")" + circuit.getName() + "(";

			for (Pin outPin : drawableCircuit.getOutPins()) {

				if (!pins.containsKey(outPin))
					pins.put(outPin, pins.size() + 1);
				datapart += String.valueOf(pins.get(outPin) + ",");

			}

			datapart = "(" + datapart.substring(1, datapart.length() - 1) + ")";
			circuits.add(datapart);
		}

		ArrayList<String> endNodes = new ArrayList<>();
		for (Pin pin : panel.pinsR) {

			String datapart = "(";
			datapart += String.valueOf(pins.get(pin.getPreviousPin()));
			datapart += ")(";
			datapart += String.valueOf(pins.get(pin));
			datapart += ")";

			endNodes.add(datapart);

		}

		ArrayList<String[]> returnList = new ArrayList<>();
		returnList.add(circuits.toArray(new String[circuits.size()]));
		returnList.add(endNodes.toArray(new String[endNodes.size()]));

		return returnList;
	}

}
