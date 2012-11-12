/* $Id: KarmaIndicator.java,v 1.11 2012/07/13 18:16:35 kiheru Exp $ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.stats;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * A bar indicator component for karma.
 */
public class KarmaIndicator extends JComponent implements PropertyChangeListener {
	private static final long serialVersionUID = 3462088641737184898L;

	private static KarmaIndicator instance;

	/** 
	 * Scaling factor for interpreting karma to bar length. Smaller means
	 * smaller change in karma bar for a karma change. 
	 */
	private static final double SCALING = 0.02;
	private static final String IMAGE_FILE_NAME = "data/gui/karma_scale.png";
	
	/** Karma scaled to pixels */
	private int karma;
	private final Sprite image;
	
	/**
	 * Create the KarmaIndicator instance.
	 * 
	 * @return instance
	 */
	static synchronized KarmaIndicator create() {
		if (instance == null) {
			instance = new KarmaIndicator();
		} else {
			throw new IllegalStateException("Instance already created");
		}
		
		return instance;
	}
	
	/**
	 * Create a new karma indicator.
	 */
	private KarmaIndicator() {
		setVisible(false);
		final SpriteStore store = SpriteStore.get();
		image = store.getSprite(IMAGE_FILE_NAME);
		
		// We don't draw the background
		setOpaque(false);
	}

	/**
	 * Gets the instance. It is invalid to call this method before the indicator
	 * instance has been initialized with create()
	 *
	 * @return KarmaIndicator
	 */
	public static KarmaIndicator get() {
		if (instance == null) {
			throw new IllegalStateException("KarmaIndicator not initialized");
		}
		return instance;
	}

	/**
	 * Set the karma value. This method may be called outside the event dispatch
	 * thread.
	 * 
	 * @param karma
	 */
	void setValue(double karma) {
		setToolTipText(describeKarma(karma));
		this.karma = scale(karma);
		repaint();
	}
	
	/**
	 * Get textual description of karma value.
	 * 
	 * @param karma
	 * @return karma description
	 */
	private String describeKarma(double karma) {
		if (karma > 499) {
			return "You have unusually good karma";
		} else if (karma > 99) {
			return "You have great karma";
		} else if (karma > 5) {
			return "You have good karma";
		} else if (karma > -5) {
			return "You have average karma";
		} else if (karma > -99) {
			return "You have bad karma";
		} else if (karma > -499) {
			return "You have terrible karma";
		}
		return "You have disastrously bad karma";
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension pref = new Dimension(image.getWidth(), image.getHeight());
		
		Insets insets = getInsets();
		pref.width += insets.left + insets.right;
		pref.height += insets.top + insets.bottom;
		
		return pref;
	}
	
	@Override
	public Dimension getMinimumSize() {
		// Preferred is also the minimum size where the bar can be drawn properly
		return getPreferredSize();
	}
	
	/**
	 * Scale a karma value to bar length.
	 * 
	 * @param karma player karma
	 * @return length of the drawn bar in pixels
	 */
	private int scale(double karma) {
		// Scale to ]0, 1[
		double normalized = 0.5 + Math.atan(SCALING * karma) / Math.PI;
	
		// ...and then to ]0, image.getWidth()[
		return (int) (image.getWidth() * normalized);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Insets insets = getInsets();
		// Paint black what is not covered by the colored bar
		g.setColor(Color.BLACK);
		g.fillRect(insets.left, insets.top, image.getWidth(), image.getHeight());
		// Draw appropriate length of the image
		g.clipRect(insets.left, insets.top, karma, getHeight());
		image.draw(g, insets.left, insets.top);
	}


	public void propertyChange(PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}

		// disable
		Map<?, ?> oldMap = (Map<?, ?>) evt.getOldValue();
		if ((oldMap != null) && oldMap.containsKey("karma_indicator")) {
			// Feature changes are triggered from outside the EDT.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setVisible(false);
				}
			});
		}

		// enable
		Map<?, ?> newMap = (Map<?, ?>) evt.getNewValue();
		if ((newMap != null) && newMap.containsKey("karma_indicator")) {
			// Feature changes are triggered from outside the EDT.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setVisible(true);
				}
			});
		}
	}
}
