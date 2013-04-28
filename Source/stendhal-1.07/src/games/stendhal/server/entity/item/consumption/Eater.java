/* $Id: Eater.java,v 1.9 2011/04/19 19:59:07 kymara Exp $ */
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
package games.stendhal.server.entity.item.consumption;

import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

class Eater implements Feeder {

	public boolean feed(final ConsumableItem item, final Player player) {
		if (player.isChokingToDeath()) {
			int playerHP = player.getHP();
			int chokingDamage = Rand.rand(2 * playerHP / 3);
			player.setHP(playerHP - chokingDamage);
			player.sendPrivateText(NotificationType.NEGATIVE, "You ate so much that you vomited on the ground and lost " + Integer.toString(chokingDamage) + " health points.");
			final Item sick = SingletonRepository.getEntityManager().getItem("vomit");
			player.getZone().add(sick);
			sick.setPosition(player.getX(), player.getY() + 1);
			player.clearFoodList();
			player.notifyWorldAboutChanges();
			return false;
		}
		
		if (player.isChoking()) {
			// remove some HP so they know we are serious about this
			int playerHP = player.getHP();
			int chokingDamage = Rand.rand(playerHP / 3);
			player.setHP(playerHP - chokingDamage);
			player.sendPrivateText(NotificationType.NEGATIVE, "You eat so much at once that you choke on your food and lose " + Integer.toString(chokingDamage) + " health points. If you eat more you could be very sick.");
			player.notifyWorldAboutChanges();
		} else if (player.isFull()) {
			player.sendPrivateText("You are now full and shouldn't eat any more.");
		} 
		player.eat((ConsumableItem) item.splitOff(1));
		return true;
	}

}
