/* $Id: BarmanNPC.java,v 1.17 2010/10/28 22:16:01 kymara Exp $ */
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
package games.stendhal.server.maps.athor.cocktail_bar;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Cocktail Bar at the Athor island beach (Inside / Level 0).
 *
 * @author kymara
 */
public class BarmanNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildBar(zone, attributes);
	}

	private void buildBar(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC barman = new SpeakerNPC("Pedro") {

			@Override
			protected void createPath() {
			        final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(8, 5));
				nodes.add(new Node(11, 5));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I #mix cocktails!");
				addQuest("What you say?");
				addOffer("Perhaps I can #mix a nice #coconut and #pineapple cocktail to cool you down...");
				addReply("pineapple","Sadly pineapples don't grow on Athor, you'll have to hunt me some yourself.");
				addReply("coconut","I'll use the milk to #mix your cocktail, look for them under palm trees.");
				addHelp("You want a pina colada mixed, I'm your man!");
				addGoodbye("Cheers!");

				// make cocktail!
				// (uses sorted TreeMap instead of HashMap)
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("coconut", 1);
				requiredResources.put("pineapple", 1);
				final ProducerBehaviour mixerBehaviour = new ProducerBehaviour("barman_mix_pina",
						"mix", "pina colada", requiredResources, 2 * 60);
				new ProducerAdder().addProducer(this, mixerBehaviour, "Aloha!");
			}
		};

		barman.setEntityClass("barmannpc");
		barman.setPosition(8, 5);
		barman.initHP(100);
		barman.setDescription("You see Pedro, the bartender. He can mix the finest cocktails for you.");
		zone.add(barman);
	}
}
