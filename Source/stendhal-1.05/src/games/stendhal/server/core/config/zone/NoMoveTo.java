/* $Id: NoMoveTo.java,v 1.2 2010/09/19 02:22:42 nhnb Exp $ */
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
package games.stendhal.server.core.config.zone;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.Map;

/**
 * Semos Jail - Level -3.
 * 
 * @author hendrik
 */
public class NoMoveTo implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		zone.setMoveToAllowed(false);
	}
}
