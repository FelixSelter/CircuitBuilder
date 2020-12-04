package com.felixselter.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;

public class CircuitButton extends JButton implements ActionListener {

	private Circuit circuit;

	public CircuitButton(Circuit circuit) {
		this.circuit = circuit;
		addActionListener(this);
		setLayout(new BorderLayout());
		setBackground(new Color(15, 15, 15));
		setForeground(Color.LIGHT_GRAY);
		setText(circuit.getName());

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setFont(new Font("", 0, getHeight() / 2));
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Main.main.frame.contentpanel.circuitpanel.selectedPin == null) {
			Main.main.frame.contentpanel.circuitpanel.circuitOnCursor = circuit.cloneCircuit();
			Main.main.frame.contentpanel.circuitpanel.startRepainting(30);
			Main.main.frame.contentpanel.textfield.requestFocus();
		}
	}

}
