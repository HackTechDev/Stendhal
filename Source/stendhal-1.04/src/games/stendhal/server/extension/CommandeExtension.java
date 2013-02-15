package games.stendhal.server.extension;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * CommandeExtension
 *
 */

public class CommandeExtension extends StendhalServerExtension implements ActionListener {

	private static final Logger logger = Logger.getLogger(CommandeExtension.class);

	public CommandeExtension() {
		super();
		logger.info("CommandeExtension en-cours de démarrage...");
		CommandCenter.register("commande", this);
		System.out.println("CommandeExtension en-cours de démarrage...");
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
		final String usage = "Usage : #/commande <nom de la commande>";
		String text = "";

		boolean canActiverCommande = false;

		String activerCommande = null;

		if (action.has("target")) {
			activerCommande = action.get("target");
			if (activerCommande.length() == 0) {
				activerCommande = null;
			}
			if (activerCommande == null) {
				player.sendPrivateText("Vous n'avez pas dit de #commande à activer.");
				logger.error("Le joueur n'a pas dit de #commande.");
			} else {
				player.sendPrivateText("Activation de la #commande en cours...");
				System.out.println("Activation de #commande en cours...");
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
		// Liste commande
		Map<String, String> listeCommande = new HashMap<String, String>();
		
		listeCommande.put("information", "Information sur #commande");
		listeCommande.put("liste", "Liste des #commande disponibles");
		listeCommande.put("aide", "Aide sur #commande");
		listeCommande.put("executer", "Executer une #commande");
		listeCommande.put("cacher", "Cacher une #commande");
		listeCommande.put("date", "Affiche la date");
		listeCommande.put("heure", "Affiche l'heure");
		listeCommande.put("dh", "Affiche la date et l'heure");

		// Date et heure
		Date dateMaintenant = new Date();
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatHeure = new SimpleDateFormat("hh:mm:ss");

		System.out.println("************************************");

		// Commandes
		if (commande.contains("information")) {
			player.sendPrivateText("information");
			System.out.println("Argument : information");
		} else if (commande.contains("liste")){
			player.sendPrivateText("liste");
			System.out.println("Argument : liste");
			// Liste les commandes
			for (Map.Entry<String,String> commandeLigne : listeCommande.entrySet()){
			    System.out.println(commandeLigne.getKey() + " = " + commandeLigne.getValue());
			}
		} else if (commande.contains("date")){
			player.sendPrivateText("date : " + formatDate.format(dateMaintenant));
			System.out.println("Argument : date : " + formatDate.format(dateMaintenant));		
		} else if (commande.contains("heure")){
			player.sendPrivateText("heure : " + formatHeure.format(dateMaintenant));
			System.out.println("Argument : heure : " + formatHeure.format(dateMaintenant));
		} else if (commande.contains("dh")){
			player.sendPrivateText(formatDate.format(dateMaintenant) + " a " + formatHeure.format(dateMaintenant));
			System.out.println(formatDate.format(dateMaintenant) + " a " + formatHeure.format(dateMaintenant));
		} else {
			player.sendPrivateText("La #commande que vous essayer d'activer n'existe pas.");
			System.out.println("La #commande que vous essayer d'activer n'existe pas.");
		}
		
		System.out.println("************************************");		
	}

}
