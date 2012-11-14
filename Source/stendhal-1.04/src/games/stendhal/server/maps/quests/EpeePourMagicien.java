/* $Id: ArmorForMagicien.java,v 1.51 2010/12/29 18:58:49 nhnb Exp $ */
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
package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Armor for Magicien
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Magicien </li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Magicien vous demande de trouver une épée de ninja.</li>
 * <li>Vous récupérez l'épée dans une salle secréte.</li>
 * <li>Quand le Magicien voit l'épée, il vous la demande et vous remerçie.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>50 XP</li>
 * <li>80 gold</li>
 * <li>Karma: 10</li>
 * <li>Access to vault</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class EpeePourMagicien extends AbstractQuest {

	private static final String QUEST_SLOT = "epee_magicien";

	

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("J'ai rencontré le Magicien. Il est dans la salle secréte.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("Il m'a  demandé de trouvé une épee de Ninja mais j'ai refusé sa demande.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("Je lui ai promis de trouver une épée de Ninja qui lui a été volé.");
		}
		if (("start".equals(questState) && (player.isEquipped("epee ninja") || player.isEquipped("epee noire ninja"))) || "done".equals(questState)) {
			res.add("J'ai trouvé une épée de Ninja, l'ai pris pour la donner au Magicien.");
		}
		if ("done".equals(questState)) {
			res.add("J'ai rapporté une épée de Ninja au Magicien. Comme remerçiement, il m'a permis d'utiliser une salle secréte.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Magicien");

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED, 
			"J'ai un peu peur d'être volé. Je n'ai aucune protection. Est-ce que vous pensez que vous pouvez m'aider ?",
			null);

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"Merci beaucoup pour l'épée de Ninja !",
			null);

		// player is willing to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"J'aimerais bien récupérer une #'epee ninja'. Si vous m'en trouvez une, je vous donnerais une récompense.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));

		// player is not willing to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Cela ne fait rien, vous n'êtes pas assez expérimenté pour m'aider.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// player wants to know what a leather cuirass is
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("epee ninja", "epee", "ninja"),
			null,
			ConversationStates.ATTENDING,
			"Une épée de Ninja peut être trouvé dans une salle secréte.",
			null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Magicien");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestInStateCondition(QUEST_SLOT, "start"),
				new OrCondition(
					new PlayerHasItemWithHimCondition("epee ninja"),
					new PlayerHasItemWithHimCondition("epee noire ninja"))),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			"Avez-vous une épee de Ninja ?",
			null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestInStateCondition(QUEST_SLOT, "start"), 
				new NotCondition(new OrCondition(
					new PlayerHasItemWithHimCondition("epee ninja"),
					new PlayerHasItemWithHimCondition("epee noire ninja")))),
			ConversationStates.ATTENDING, 
			"Je serais content de recevoir une épee de Ninja. Comment puis-je vous aider #help ?",
			null);

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new EquipItemAction("money", 80));
		reward.add(new IncreaseXPAction(50));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction(10));

		final List<ChatAction> reward1 = new LinkedList<ChatAction>(reward);
		reward1.add(new DropItemAction("epee ninja"));

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			// make sure the player isn't cheating by putting the armor
			// away and then saying "yes"
			new PlayerHasItemWithHimCondition("epee ninja"), 
			ConversationStates.ATTENDING, "Merci beaucoup ! Pour vous remercier vous disposer maintenant une  #salle secrete.",
			new MultipleActions(reward1));

		final List<ChatAction> reward2 = new LinkedList<ChatAction>(reward);
		reward2.add(new DropItemAction("epee noire ninja"));
		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			// make sure the player isn't cheating by putting the armor
			// away and then saying "yes"
			new AndCondition(
				new NotCondition(new PlayerHasItemWithHimCondition("epee ninja")),
				new PlayerHasItemWithHimCondition("pauldroned leather cuirass")), 
			ConversationStates.ATTENDING, "Merci beaucoup ! Pour vous remercier vous disposer maintenant une  #salle secrete.",
			new MultipleActions(reward2));

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"J'espére que vous allez trouvez une épée noire de Ninja pour moi.",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Epee Pour Magicien",
				"Le Magicien a besoin d'une épée noire de Ninja.",
				false);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "EpeePourMagicien";
	}
	
	@Override
	public int getMinLevel() {
		return 0;
	}
}
