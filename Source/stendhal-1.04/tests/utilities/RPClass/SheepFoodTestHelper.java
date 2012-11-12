/* $Id: SheepFoodTestHelper.java,v 1.3 2010/09/19 01:28:49 nhnb Exp $ */
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
package utilities.RPClass;

import marauroa.common.game.RPClass;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;

public class SheepFoodTestHelper {
	public static void generateRPClasses() {
		PassiveEntityRespawnPointTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("food")) {
			SheepFood.generateRPClass();
		}

	}
}
