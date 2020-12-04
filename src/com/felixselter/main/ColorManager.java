package com.felixselter.main;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ColorManager extends Properties {

	public static ColorManager instance;
	private File layout;

	public ColorManager(File layout) {
		instance = this;
		this.layout = layout;
		try {
			if(layout.exists())
			loadFromXML(new FileInputStream(layout));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Color getColorOf(String id) {
		String[] values = getProperty(id).split(",");
		if (values.length == 3) {
			return new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]));
		} else if (values.length == 4) {
			return new Color(Integer.valueOf(values[0]), Integer.valueOf(values[1]), Integer.valueOf(values[2]),
					Integer.valueOf(values[3]));
		}
		return null;
	}

	public void setColor(String id, Color color) {
		setProperty(id, color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha());
		try {
			storeToXML(new FileOutputStream(layout), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
