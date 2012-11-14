package games.stendhal.server.maps.semos.salle03;
 
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
 
public class TechnoguerrierNPC implements ZoneConfigurator {
 
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

       private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
    	    final SpeakerNPC npc = new SpeakerNPC("Technoguerrier") {

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

                // Utilise le "goodbye" standard, mais vous pouvez en définir un autre dans les ()
                addGoodbye();
            }
        };
 
        // Cela détermine comme le PNJ ressemble. armoredleadernpc.png est une image du répertoire data/sprites/npc/
        npc.setEntityClass("armoredleadernpc");
        // Initialise une description quand le joueur regarde (#look) le PNJ.
        npc.setDescription("Vous voyer un soigneur, il semble un tout petit peu occupé en ce moment mais il pourra peut-être vous aider.");
        // Initialise la position initial au premier noeud du chemon que vous avez définé ci-dessus
        npc.setPosition(9, 5);
        npc.initHP(100);
 
        zone.add(npc);   
    }
}
