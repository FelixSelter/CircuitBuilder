package com.felixselter.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class CircuitPanel extends JPanel implements MouseListener, KeyListener, MouseMotionListener {

	public ArrayList<Pin> pinsL = new ArrayList<>();
	public ArrayList<Pin> pinsR = new ArrayList<>();
	private int toleranz = 10;
	private boolean shift = false;
	private int pinLradius;
	private int pinRradius;
	private int pinLBorder;
	private int pinRBorder;
	private int pinSpace;
	private Timer timer;
	public Circuit circuitOnCursor;
	public ArrayList<DrawableCircuit> circuits = new ArrayList<>();
	private Point mouseCoordinates;
	public Pin selectedPin;
	public HashMap<Pin, Rectangle> pins = new HashMap<>();

	public CircuitPanel() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void startRepainting(int fps) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Main.main.frame.repaint();
			}
		}, 0, 1000 / fps);
	}

	@Override
	public void paintComponent(Graphics g) {
		
		for (DrawableCircuit drawedCircuit : circuits) {
			drawedCircuit.doCalculation();
		}

		Graphics2D g2 = (Graphics2D) g;

		g.setColor(new Color(44, 44, 44));
		g.fillRect(0, 0, getWidth(), getHeight());

		pinSpace = Math.round(getHeight() / 100f);

		if (pinsL.size() > 0) {
			pinLradius = ((getHeight() / pinsL.size()) / 2) - (pinSpace / 2);
			pinLBorder = (getHeight() - (pinsL.size() * (pinLradius * 2 + pinSpace))) / 2;

			for (Pin pin : pinsL) {

				g.setColor(pin.isActive() ? ColorManager.instance.getColorOf("ActivePin")
						: ColorManager.instance.getColorOf("Pin"));
				g.fillOval(-pinLradius + 10, pinLBorder + (pinsL.indexOf(pin) * (pinLradius * 2 + pinSpace)),
						pinLradius * 2, pinLradius * 2);

				if (pin.getNextPins().size() > 0) {
					g2.setStroke(new BasicStroke(3));
					for (Pin nextPin : pin.getNextPins()) {

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

		if (pinsR.size() > 0) {
			pinRradius = ((getHeight() / pinsR.size()) / 2) - (pinSpace / 2);
			pinRBorder = (getHeight() - (pinsR.size() * (pinRradius * 2 + pinSpace))) / 2;

			for (Pin pin : pinsR) {
				if (pin.isActive()) {
					g.setColor(ColorManager.instance.getColorOf("ActivePin"));
				} else {
					g.setColor(ColorManager.instance.getColorOf("Pin"));
				}
				g.fillOval(getWidth() - pinRradius - 10,
						pinRBorder + (pinsR.indexOf(pin) * (pinRradius * 2 + pinSpace)), pinRradius * 2,
						pinRradius * 2);
			}
		}

		g.setColor(ColorManager.instance.getColorOf("Border"));
		g2.setStroke(new BasicStroke(10));
		g.drawRect(0, 0, getWidth(), getHeight());

		if (circuitOnCursor != null) {

			int pinDiameter = (getHeight() / 20);
			int circuitHeight = pinDiameter * circuitOnCursor.getMaxPinsVerticaly();
			int circuitWidth = getWidth() / 20;
			g.setColor(ColorManager.instance.getColorOf("Pin"));

			int inPinBorder = (circuitHeight - (circuitOnCursor.getInPins() * pinDiameter)) / 2;
			for (int inPin = 0; inPin < circuitOnCursor.getInPins(); inPin++) {
				g.fillOval(mouseCoordinates.x - (pinDiameter / 2),
						mouseCoordinates.y + inPinBorder * (inPin + 1) + inPin * pinDiameter, pinDiameter, pinDiameter);
			}

			int outPinBorder = (circuitHeight - (circuitOnCursor.getOutPins() * pinDiameter)) / 2;
			for (int outPin = 0; outPin < circuitOnCursor.getOutPins(); outPin++) {
				g.fillOval(mouseCoordinates.x + circuitWidth - (pinDiameter / 2),
						mouseCoordinates.y + outPinBorder * (outPin + 1) + outPin * pinDiameter, pinDiameter,
						pinDiameter);
			}

			g.setColor(circuitOnCursor.getColor());
			g.fillRect(mouseCoordinates.x, mouseCoordinates.y, circuitWidth, circuitHeight);

			g.setColor(ColorManager.instance.getColorOf("Font"));
			g.setFont(new Font("", 0, circuitWidth / circuitOnCursor.getName().length()));
			g.drawString(circuitOnCursor.getName(),
					mouseCoordinates.x + (circuitWidth / 2)
							- (g.getFontMetrics().stringWidth(circuitOnCursor.getName()) / 2),
					mouseCoordinates.y + circuitHeight / 2 + g.getFontMetrics().getMaxAscent() / 2);

		}

		if (selectedPin != null) {
			g.setColor(selectedPin.isActive() ? ColorManager.instance.getColorOf("ActivePin")
					: ColorManager.instance.getColorOf("Pin"));
			g.drawLine(pins.get(selectedPin).x + (pins.get(selectedPin).width / 2),
					pins.get(selectedPin).y + (pins.get(selectedPin).height / 2), mouseCoordinates.x,
					mouseCoordinates.y);
		}

		for (DrawableCircuit drawedCircuit : circuits) {
			drawedCircuit.draw(g, getWidth(), getHeight());
		}

		if (Main.debug) {
			g2.setStroke(new BasicStroke(1));
			for (Rectangle rec : pins.values()) {
				g.setColor(Color.red);
				g.drawRect(rec.x, rec.y, rec.width, rec.height);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Main.main.frame.repaint();

		if (e.getButton() == MouseEvent.BUTTON1) {

			if (circuitOnCursor != null) {
				circuits.add(new DrawableCircuit(circuitOnCursor, mouseCoordinates.x, mouseCoordinates.y, getWidth(),
						getHeight()));
				circuitOnCursor = null;
				timer.cancel();
				timer = null;
			} else {

				Pin pin = null;
				for (Entry<Pin, Rectangle> pinData : pins.entrySet()) {
					if (pinData.getValue().intersects(new Rectangle(e.getX(), e.getY(), 1, 1))) {
						pin = pinData.getKey();
						break;
					}
				}

				if (pin != null) {

					if (shift) {
						if (pin.isInputMode())
							pin.setActive(!pin.isActive());
					} else if (circuitOnCursor == null) {
						if (pin.isExpandable()) {
							selectedPin = pin;
							startRepainting(30);
						} else if (selectedPin != null) {
							pin.setActive(selectedPin.isActive());
							selectedPin.addNextPin(pin);
							selectedPin = null;
							timer.cancel();
							timer = null;
						}
					}
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {

			if (shift) {

				if (e.getX() < toleranz && pinsL.size() > 0) {
					Pin pin = pinsL.get(pinsL.size() - 1);
					pinsL.remove(pin);
					pins.remove(pin);
					updatePinLHitboxes();
				} else if (e.getX() > getWidth() - toleranz && pinsR.size() > 0) {
					Pin pin = pinsR.get(pinsR.size() - 1);
					pinsR.remove(pin);
					pins.remove(pin);
					updatePinRHitboxes();
				}

			} else {

				if (e.getX() < toleranz) {

					Pin pin = new Pin(true, true);
					pinsL.add(pin);
					updatePinLHitboxes();

				} else if (e.getX() > getWidth() - toleranz) {

					Pin pin = new Pin(false, false);
					pinsR.add(pin);
					updatePinRHitboxes();

				}
			}
		}
	}

	public void updatePinRHitboxes() {
		for (Pin pinR : pinsR) {
			pins.remove(pinR);

			pinSpace = Math.round(getHeight() / 100f);
			pinRradius = ((getHeight() / pinsR.size()) / 2) - (pinSpace / 2);
			pinRBorder = (getHeight() - (pinsR.size() * (pinRradius * 2 + pinSpace))) / 2;

			pins.put(pinR, new Rectangle(getWidth() - pinRradius - 10,
					pinRBorder + (pinsR.indexOf(pinR) * (pinRradius * 2 + pinSpace)), pinRradius * 2, pinRradius * 2));
		}
	}

	public void updatePinLHitboxes() {
		for (Pin pinL : pinsL) {
			pins.remove(pinL);

			pinSpace = Math.round(getHeight() / 100f);
			pinLradius = ((getHeight() / pinsL.size()) / 2) - (pinSpace / 2);
			pinLBorder = (getHeight() - (pinsL.size() * (pinLradius * 2 + pinSpace))) / 2;

			pins.put(pinL, new Rectangle(-pinLradius + 10,
					pinLBorder + (pinsL.indexOf(pinL) * (pinLradius * 2 + pinSpace)), pinLradius * 2, pinLradius * 2));
		}

	}

	public void updateCircuitPins() {
		for (DrawableCircuit drawableCircuit : circuits) {
			drawableCircuit.updatePins(getWidth(), getHeight());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT && !shift) {
			shift = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			shift = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
		mouseClicked(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseCoordinates = e.getPoint();
	}
}
