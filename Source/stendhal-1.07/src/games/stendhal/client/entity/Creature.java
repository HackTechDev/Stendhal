/* $Id: Creature.java,v 1.88 2013/01/19 21:53:18 kiheru Exp $ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.common.constants.SoundLayer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

public class Creature extends AudibleEntity {
	/**
	 * Debug property.
	 */
	public static final Property PROP_DEBUG = new Property();

	/**
	 * Metamorphosis property.
	 */
	public static final Property PROP_METAMORPHOSIS = new Property();

	@Override
	protected void nonCreatureClientAddEventLine(final String text) {
		
		// no logging for Creature "sounds" in the client window
	}

	/**
	 * The current debug info.
	 */
	private String debug;

	/**
	 * The current metamorphosis.
	 */
	private String metamorphosis;

	//
	// Creature
	//

	public String getDebug() {
		return debug;
	}

	/**
	 * Get the metamorphosis in effect.
	 * 
	 * @return The metamorphosis, or <code>null</code>.
	 */
	public String getMetamorphosis() {
		return metamorphosis;
	}

	//
	// Entity
	//

	/**
	 * Get the area the entity occupies.
	 * 
	 * @return A rectange (in world coordinate units).
	 */
	@Override
	public Rectangle2D getArea() {
		// Hack for human like creatures
		if ((Math.abs(getWidth() - 1.0) < 0.1)
				&& (Math.abs(getHeight() - 2.0) < 0.1)) {
			return new Rectangle.Double(getX(), getY() + 1.0, 1.0, 1.0);
		}

		return super.getArea();
	}

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param object
	 *            The object.
	 * 
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		if (object.has("name")) {
			addSounds(SoundLayer.FIGHTING_NOISE.groupName, "attack",
				"punch-1"   , "punch-2", "punch-3",
				"punch-4"   , "punch-5", "punch-6",
				"swingaxe-1", "slap-1" , "arrow-1");
		}

		if (object.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
		} else {
			metamorphosis = null;
		}
	}

	@Override
	public void onDamaged(Entity attacker, int damage) {
		super.onDamaged(attacker, damage);
		playRandomSoundFromCategory(SoundLayer.FIGHTING_NOISE.groupName, "attack");
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 * 
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		/*
		 * Debuging?
		 */
		if (changes.has("debug")) {
			debug = changes.get("debug");
			fireChange(PROP_DEBUG);
		}

		if (changes.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
			fireChange(PROP_METAMORPHOSIS);
		}
	}

	/**
	 * The object removed attribute(s).
	 * 
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		if (changes.has("metamorphosis")) {
			metamorphosis = null;
			fireChange(PROP_METAMORPHOSIS);
		}
	}
}
