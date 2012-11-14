package games.stendhal.server.extension;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class MagieExtension extends StendhalServerExtension implements
		ActionListener {

	private static final Logger logger = Logger.getLogger(MagieExtension.class);

	public MagieExtension() {
		super();
		logger.info("MagieExtension en-cours de démarrage...");
		CommandCenter.register("sort", this);

	}

	@Override
	public void init() {
	}

	public void onAction(final Player player, final RPAction action) {
		final String type = action.get("type");

		if (type.equals("sort")) {
			onSort(player, action);
		}
	}

	private void onSort(final Player player, final RPAction action) {
		final String usage = "Usage : #/sort <nom du sort>";
		String text = "";

		boolean canJeterSort = false;

		String jeterSort = null;

		if (action.has("target")) {
			jeterSort = action.get("target");
			if (jeterSort.length() == 0) {
				jeterSort = null;
			}
			if (jeterSort == null) {
				player.sendPrivateText("Vous n'avez dit de sort à jeter.");
				logger.error("Le joueur n'a pas dit de sort.");
			} else {
				player.sendPrivateText("Lancer de sort en cours...");
			}
		} else {
			text = usage;
		}

		if (jeterSort != null) {
			// the list of sorts
			final String availableSorts = player.getQuest("sorts");

			if (availableSorts == null) {
				player.sendPrivateText("Vous ne pouvez pas lancer ce sort.");
				return;
			}
	
			// Checks to see if the list of sorts available to the player contains
			// the sort they tried to cast
			if (availableSorts.contains(jeterSort)) {
				canJeterSort = true;
			} else {
				player.sendPrivateText("Vous ne pouvez pas lancer ce sort.");
			}
		}

		if (canJeterSort) {
			jeterSort(player, jeterSort);
		}

		if (text.length() > 0) {
			player.sendPrivateText(text);
		}
	}

	private void jeterSort(final Player player, String sort) {
		// put sorts and actions here
		if (sort.contains("maladie")) {
			player.sendPrivateText("Sort de Maladie");
/*
			if (player.getMana() > 15) {
				player.heal();

				final int mana = player.getMana();
				final int newmana = mana - 15;
				player.setMana(newmana);
				player.sendPrivateText("You have been healed.");
				player.update();
				player.notifyWorldAboutChanges();
			} else {
				player.sendPrivateText("You do not have enough available mana to use this sort.");
			}
*/
		} else if (sort.contains("diminuer stats")) {

			if (player.getMana() >= 110) {

				// gets old stats
				int oldLevel = player.getLevel();
				final int oldXP = player.getXP();
				final int oldDefXP = player.getDefXP();
				int oldDef = player.getDef();
				int oldAtk = player.getAtk();
				final int oldAtkXP = player.getAtkXP();

				// gets new stats
				final int newLevel = oldLevel + 1;
				final int newXP = oldXP + 44900;
				final int newDefXP = oldDefXP + 24700;
				final int newDef = oldDef + 1;
				final int newAtkXP = oldAtkXP + 24700;
				final int newAtk = oldAtk + 1;

				// sets new stats
				player.setXP(newXP);
				player.setLevel(newLevel);	// if not directly, the level will automatically be increased in updateLevel()
				player.setDefXP(newDefXP);
				player.setDef(newDef);		// if not directly set, the DEF value will automatically be increased in setDefXP()
				player.setAtk(newAtk);
				player.setAtkXP(newAtkXP);	// if not directly set, the ATK value will automatically be increased in setAtkXP()

				// saves changes
				player.update();
				player.notifyWorldAboutChanges();

				// takes away mana
				final int mana = player.getMana();
				final int newmana = mana - 110;
				player.setMana(newmana);

				player.sendPrivateText("Your stats have been raised.");

			} else {
				player.sendPrivateText("Vous n'avez pas assez de mana pour lancer ce sort");
			}
		} else {
			player.sendPrivateText("Le sort que vous essayer de lancer n'existe pas.");
		}
	}

}
