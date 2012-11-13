/* $Id: MagicienPNJ.java,v 1.0 2011/01/12 21:58:00 samuelgondouin Exp $ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

// stendhal-0.91/src/games/stendhal/server/maps/semos/salle01/MagicienNPC.java

package games.stendhal.server.maps.semos.salle01;
 
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
// Paquetage car le PNJ est un vendeur
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
// Paquetage car le PNJ est un vendeur
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
// Paquetage car le PNJ est un soigneur
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
// Paquetage car le PNJ est un vendeur
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
 
public class MagicienNPC implements ZoneConfigurator {
        // Le PNJ a un magasin pour vendre des potions
	private final ShopList shops = SingletonRepository.getShopList();
 
	/**
	 * Configurer une zone.
	 *
	 * @param	zone		La zone à configurer.
	 * @param	attributes	Configuration des attributs.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

       private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
    	    final SpeakerNPC npc = new SpeakerNPC("Magicien") {

	    @Override
            protected void createPath() {
                List<Node> nodes=new LinkedList<Node>();
                nodes.add(new Node(9,6));
                nodes.add(new Node(9,10));
		setPath(new FixedPath(nodes, true));
            }

		@Override 
            protected void createDialog() {
		// Le PNJ réponds par un bonjour quand le joueur le salut. Mais vous pouvez mettre un salut personnalisé entre les ()
                addGreeting();
                // La réponse du PNJ quand le joueur lui dit #job
                addJob("J'ai le talent de soigner les joueurs blessé. Je vend aussi des #potions et des antidotes.");
                // La réponse du PNJ quand le joueur lui demande l'aide
                addHelp("Demandez-moi de vous soigner  (#heal), je ferais ou demandez-moi (#offer) quelques choses et je vous monterais ce que j'ai comment choses dans mon magasin.");
                // Fait que le PNJ vends des potions ou des antidotes
		new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("healing")));

                // Le PNJ soigne le joueur pour une pièce
                new HealerAdder().addHealer(this, -2);
		// Répond grâce un mot spécial déclencheur
                addReply("potions","Demandez-moi (#offer) ce que j'ai'.");
                // Utilise le "goodbye" standard, mais vous pouvez en définir un autre dans les ()
                addGoodbye();
            }
        };
 
        // Cela détermine comme le PNJ ressemble. chiefassassinnpc.png est une image du répertoire data/sprites/npc/
        npc.setEntityClass("chiefassassinnpc");
        // Initialise une description quand le joueur regarde (#look) le PNJ.
        npc.setDescription("Vous voyer un soigneur, il semble un tout petit peu occupé en ce moment mais il pourra peut-être vous aider.");
        // Initialise la position initial au premier noeud du chemon que vous avez définé ci-dessus
        npc.setPosition(9, 5);
        npc.initHP(100);
 
        zone.add(npc);   
    }
}
