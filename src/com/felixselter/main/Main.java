package com.felixselter.main;

import java.io.File;

public class Main {

	public static boolean debug = false;
	public static Main main;
	public Frame frame;

	public Main() {
		main = this;
		new ColorManager(new File("layout.xml"));
		if (!new File("Circuits").exists())
			new File("Circuits").mkdir();
		this.frame = new Frame();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}

}
