/* $Id: Attacker.java,v 1.3 2013/03/27 13:34:49 antumdeluge Exp $ */
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

import games.stendhal.server.entity.RPEntity;

public interface Attacker {
	boolean attack(RPEntity victim);
	
	void applyAntipoison(double antipoison);
	
	int getProbability();
	
	void setProbability(int p);

}
