package com.mpcmaid.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 * A pad drawn like a hardware MPC pad: a rounded square with its name in the
 * corner and the names of its samples, tinted when it holds a sample and
 * outlined in the accent color when selected.
 *
 * Colors come from the current look and feel, so it follows light and dark
 * mode.
 */
public class PadButton extends JButton {

	private static final int ARC = 12;

	private static final int INSET = 8;

	private final String padName;

	private String[] sampleNames = new String[0];

	public PadButton(String padName) {
		this.padName = padName;
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
		setOpaque(false);
	}

	public Dimension getPreferredSize() {
		return new Dimension(90, 90);
	}

	/**
	 * @param sampleNames
	 *            The sample name of each layer, empty for unused layers
	 */
	public void setSampleNames(String[] sampleNames) {
		this.sampleNames = sampleNames;
		final StringBuffer tip = new StringBuffer("<html><b>" + padName + "</b>");
		for (int i = 0; i < sampleNames.length; i++) {
			if (sampleNames[i].length() > 0) {
				tip.append("<br>").append(i + 1).append(": ").append(sampleNames[i]);
			}
		}
		setToolTipText(tip.append("</html>").toString());
		repaint();
	}

	private boolean hasSample() {
		for (int i = 0; i < sampleNames.length; i++) {
			if (sampleNames[i].length() > 0) {
				return true;
			}
		}
		return false;
	}

	protected void paintComponent(Graphics graphics) {
		final Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		final Color surface = color("TextField.background", Color.WHITE);
		final Color accent = color("Component.accentColor", color("Component.focusColor", new Color(0x007AFF)));
		final Color foreground = color("Label.foreground", Color.BLACK);
		final Color secondary = color("Label.disabledForeground", Color.GRAY);

		final ButtonModel model = getModel();
		Color fill = hasSample() ? mix(surface, accent, 0.18f) : surface;
		if (model.isPressed() && model.isArmed()) {
			fill = mix(surface, accent, 0.45f);
		}

		final float w = getWidth() - 3;
		final float h = getHeight() - 3;
		final RoundRectangle2D shape = new RoundRectangle2D.Float(1.5f, 1.5f, w, h, ARC, ARC);
		g.setColor(fill);
		g.fill(shape);
		if (isSelected()) {
			g.setColor(accent);
			g.setStroke(new BasicStroke(2.5f));
		} else {
			g.setColor(mix(fill, foreground, 0.12f));
			g.setStroke(new BasicStroke(1f));
		}
		g.draw(shape);

		// pad name in the top left corner
		final Font baseFont = getFont();
		g.setFont(baseFont.deriveFont(Font.BOLD, baseFont.getSize2D() - 1));
		g.setColor(isSelected() ? accent : secondary);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(padName, INSET, INSET + fm.getAscent());

		// sample names from the bottom up, layer 1 on top
		g.setFont(baseFont.deriveFont(Font.PLAIN, baseFont.getSize2D() - 1));
		fm = g.getFontMetrics();
		final int maxWidth = getWidth() - 2 * INSET;
		int lines = 0;
		for (int i = 0; i < sampleNames.length; i++) {
			if (sampleNames[i].length() > 0) {
				lines++;
			}
		}
		int y = getHeight() - INSET - (lines - 1) * fm.getHeight() - fm.getDescent();
		boolean first = true;
		for (int i = 0; i < sampleNames.length; i++) {
			if (sampleNames[i].length() == 0) {
				continue;
			}
			g.setColor(first ? foreground : secondary);
			g.drawString(fit(sampleNames[i], fm, maxWidth), INSET, y);
			y += fm.getHeight();
			first = false;
		}
		g.dispose();
	}

	/**
	 * Shortens the name in the middle so that its distinctive end stays
	 * readable, e.g. "Kick_Ac...Dry01" with a single ellipsis character
	 */
	private static String fit(String name, FontMetrics fm, int maxWidth) {
		if (fm.stringWidth(name) <= maxWidth) {
			return name;
		}
		final String ellipsis = "\u2026";
		int keep = name.length() - 1;
		while (keep > 1) {
			final int head = (keep + 1) / 2;
			final String candidate = name.substring(0, head) + ellipsis + name.substring(name.length() - (keep - head));
			if (fm.stringWidth(candidate) <= maxWidth) {
				return candidate;
			}
			keep--;
		}
		return ellipsis;
	}

	private static Color color(String key, Color fallback) {
		final Color c = UIManager.getColor(key);
		return c != null ? c : fallback;
	}

	private static Color mix(Color a, Color b, float amount) {
		final float r = a.getRed() + (b.getRed() - a.getRed()) * amount;
		final float g = a.getGreen() + (b.getGreen() - a.getGreen()) * amount;
		final float bl = a.getBlue() + (b.getBlue() - a.getBlue()) * amount;
		return new Color(Math.round(r), Math.round(g), Math.round(bl));
	}

	public String toString() {
		return "PadButton " + padName;
	}

}
