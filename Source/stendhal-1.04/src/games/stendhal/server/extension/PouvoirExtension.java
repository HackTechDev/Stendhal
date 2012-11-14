package games.stendhal.server.extension;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class PouvoirExtension extends StendhalServerExtension implements
		ActionListener {

	private static final Logger logger = Logger.getLogger(PouvoirExtension.class);

	public PouvoirExtension() {
		super();
		logger.info("PouvoirExtension en-cours de démarrage...");
		CommandCenter.register("pouvoir", this);

	}

	@Override
	public void init() {
	}

	public void onAction(final Player player, final RPAction action) {
		final String type = action.get("type");

		if (type.equals("pouvoir")) {
			onPouvoir(player, action);
		}
	}

	private void onPouvoir(final Player player, final RPAction action) {
		final String usage = "Usage : #/pouvoir <nom du pouvoir>";
		String text = "";

		boolean canActiverPouvoir = false;

		String activerPouvoir = null;

		if (action.has("target")) {
			activerPouvoir = action.get("target");
			if (activerPouvoir.length() == 0) {
				activerPouvoir = null;
			}
			if (activerPouvoir == null) {
				player.sendPrivateText("Vous n'avez dit de pouvoir à activer.");
				logger.error("Le joueur n'a pas dit de pouvoir.");
			} else {
				player.sendPrivateText("Activation du pouvoir en cours...");
			}
		} else {
			text = usage;
		}

		if (activerPouvoir != null) {
			final String availablePouvoirs = player.getQuest("pouvoirs");

			canActiverPouvoir = true;
		}

		if (canActiverPouvoir) {
			activerPouvoir(player, activerPouvoir);
		}

		if (text.length() > 0) {
			player.sendPrivateText(text);
		}
	}

	private void activerPouvoir(final Player player, String pouvoir) {
		if (pouvoir.contains("SuperSouffle")) {
			player.sendPrivateText("SuperSouffle");
		} else {
			player.sendPrivateText("Le pouvoir que vous essayer d'activer n'existe pas.");
		}
	}

}
