/* $Id: Poisoner.java,v 1.5 2013/03/27 13:34:49 antumdeluge Exp $ */
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
package games.stendhal.server.entity.creature.impl.poison;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class Poisoner implements Attacker {
	ConsumableItem poison;
	private int probability;

	public Poisoner(final int probability, final ConsumableItem poison) {
		this.probability = probability;
		this.poison = poison;
	}

	public Poisoner() {
		// standard constructor
	}

	public boolean attack(final RPEntity victim) {
		final int roll = Rand.roll1D100();
		if (roll <= probability) {
			if (victim instanceof Player) {
				final Player player = (Player) victim;
				if (player.poison(new ConsumableItem(poison))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void applyAntipoison(double antipoison) {
		/*
		 * invert the value for multiplying
		 */
		antipoison = (1 - antipoison);
		this.probability *= antipoison;
	}
	
	public int getProbability() {
		return this.probability;
	}
	
	public void setProbability(int p) {
		this.probability = p;
	}
}
