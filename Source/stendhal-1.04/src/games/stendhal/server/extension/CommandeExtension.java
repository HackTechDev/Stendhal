package games.stendhal.server.extension;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * CommandeExtension
 *
 */

public class CommandeExtension extends StendhalServerExtension implements
		ActionListener {

	private static final Logger logger = Logger.getLogger(CommandeExtension.class);

	public CommandeExtension() {
		super();
		logger.info("CommandeExtension en-cours de démarrage...");
		CommandCenter.register("commande", this);

	}

	@Override
	public void init() {
	}

	public void onAction(final Player player, final RPAction action) {
		final String type = action.get("type");

		if (type.equals("commande")) {
			onCommande(player, action);
		}
	}

	private void onCommande(final Player player, final RPAction action) {
		final String usage = "Usage : #/commande <nom du commande>";
		String text = "";

		boolean canActiverCommande = false;

		String activerCommande = null;

		if (action.has("target")) {
			activerCommande = action.get("target");
			if (activerCommande.length() == 0) {
				activerCommande = null;
			}
			if (activerCommande == null) {
				player.sendPrivateText("Vous n'avez dit de commande à activer.");
				logger.error("Le joueur n'a pas dit de commande.");
			} else {
				player.sendPrivateText("Activation du commande en cours...");
			}
		} else {
			text = usage;
		}

		if (activerCommande != null) {
			final String availableCommandes = player.getQuest("commandes");

			canActiverCommande = true;
		}

		if (canActiverCommande) {
			activerCommande(player, activerCommande);
		}

		if (text.length() > 0) {
			player.sendPrivateText(text);
		}
	}

	private void activerCommande(final Player player, String commande) {
		if (commande.contains("Information")) {
			player.sendPrivateText("Information");
		} else {
			player.sendPrivateText("Le commande que vous essayer d'activer n'existe pas.");
		}
	}

}
