package com.felixselter.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class Frame extends JFrame {

	public ContentPanel contentpanel = new ContentPanel();

	public Frame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(contentpanel);
		pack();
		setLocationRelativeTo(null);
	}

}

class ContentPanel extends JPanel {

	public JTextField textfield = new JTextField() {
		@Override
		public void setBorder(Border border) {
		}
	};
	public CircuitPanel circuitpanel = new CircuitPanel();
	private CircuitBar circuitbar = new CircuitBar();

	private JScrollPane scrollpane = new JScrollPane(circuitbar, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) {

		@Override
		public void setBorder(Border border) {
		}
	};

	public ContentPanel() {
		setPreferredSize(new Dimension(160, 90));
		setBackground(new Color(47, 47, 47));
		setLayout(null);

		textfield.setBackground(new Color(49, 49, 49));
		textfield.setForeground(Color.white);
		textfield.setHorizontalAlignment(JTextField.CENTER);
		textfield.addKeyListener(circuitpanel);
		add(textfield);
		add(circuitpanel);
		add(scrollpane);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				textfield.setBounds(getWidth() / 36, getHeight() / 32, getWidth() - (getWidth() / 36) * 2,
						getHeight() / 16);
				textfield.setFont(new Font("", 0, getHeight() / 32));

				circuitpanel.setBounds(getWidth() / 36, getHeight() / 8, getWidth() - (getWidth() / 36) * 2,
						(int) (getHeight() - (getHeight() / 16) * 3.5));

				circuitbar.setBounds(0, 0, getWidth(), getHeight() / 16);
				scrollpane.setBounds(0, getHeight() - (getHeight() / 16), getWidth(), (getHeight() / 16));
				
				circuitpanel.updatePinLHitboxes();
				circuitpanel.updatePinRHitboxes();
				circuitpanel.updateCircuitPins();

				Main.main.frame.repaint();

			}
		});

	}

}
