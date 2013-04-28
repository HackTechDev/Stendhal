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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * QUEST: Antivenom Ring
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Jameson (the retired apothecary in semos mountain)</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Bring note to apothecary and to Jameson.</li>
 * <li>As a favor to Klaas, Jameson will help you to strengthen your medicinal ring.</li>
 * <li>Bring Jameson a medicinal ring, venom gland, 2 mandragora and 5 fairycakes.</li>
 * <li>Jameson concocts a mixture that doubles your rings' resistance against poison.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>2000 XP</li>
 * <li>antivenom ring</li>
 * <li>Karma: 25</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class AntivenomRing extends AbstractQuest {

	private static final String QUEST_SLOT = "antivenom_ring";
	
	public static final String NEEDED_ITEMS = "medicinal ring=1;venom gland=1;mandragora=2;fairy cake=5";
	
	private static final int REQUIRED_MINUTES = 30;
	
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have found the hermit apothecary's lab in Semos Mountain.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("done".equals(questState)) {
			res.add("I gathered all that Jameson asked for. He applied a special mixture to my ring which made it more resistant to poison. I also got some XP and karma.");
		}
		else if ("rejected".equals(questState)) {
			res.add("Poison is too dangerous. I do not want to get hurt.");
		}
		else if (player.getQuest(QUEST_SLOT).startsWith("enhancing;")) {
			res.add("Jameson is enhancing my ring.");
		}
		else {
			final ItemCollection missingItems = new ItemCollection();
			missingItems.addFromQuestStateString(questState);
			res.add("I still need to bring Jameson " + Grammar.enumerateCollection(missingItems.toStringList()) + ".");
		}
		return res;
	}
	
	private void prepareHintNPCs() {
		final SpeakerNPC hintNPC1 = npcs.get("Valo");
		final SpeakerNPC hintNPC2 = npcs.get("Haizen");
		final SpeakerNPC hintNPC3 = npcs.get("Ortiv Milquetoast");
		
		// Valo is asked about an apothecary
		hintNPC1.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"Hmmm, yes, I knew a man long ago who was studying medicines and antipoisons. The last I heard he was #retreating into the mountains.",
				null);
		
		hintNPC1.add(ConversationStates.ATTENDING,
				Arrays.asList("retreat", "retreats", "retreating"),
				null,
				ConversationStates.ATTENDING,
				"He's probably hiding. Keep an eye out for hidden entrances.",
				null);
		
		// Haizen is asked about an apothecary
		hintNPC2.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"Yes, there was once an estudious man in Ados. But, due to complications with leadership there he was forced to leave. I heard that he was #hiding somewhere in the Semos region.",
				null);
		
		hintNPC2.add(ConversationStates.ATTENDING,
				Arrays.asList("hide", "hides", "hiding", "hidden"),
				null,
				ConversationStates.ATTENDING,
				"If I were hiding I would surely do it in a secret room with a hidden entrance.",
				null);
		
		// Ortiv Milquetoast is asked about an apothecary
		hintNPC3.add(ConversationStates.ATTENDING,
				"apothecary",
				null,
				ConversationStates.ATTENDING,
				"You must be speaking of my colleague, Jameson. He was forced to #hide out because of problems in Ados. He hasn't told me where, but he does bring the most delicious pears when he visits.",
				null);
		
		hintNPC3.add(ConversationStates.ATTENDING,
				Arrays.asList("hide", "hides", "hiding", "hidden"),
				null,
				ConversationStates.ATTENDING,
				"He hinted at a secret laboratory that he had built. Something about a hidden doorway.",
				null);
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Jameson");
        
		// If player has note to apothecary then quest is offered
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new PlayerHasItemWithHimCondition("note to apothecary"),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.QUEST_OFFERED, 
				"Oh, a message from Klaas. Is that for me?",
				new SetQuestAction(QUEST_SLOT, "offered"));
        
		// In case player dropped note before speaking to Jameson
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new PlayerHasItemWithHimCondition("note to apothecary"),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.QUEST_OFFERED, 
				"Oh, a message from Klaas. Is that for me?",
				new SetQuestAction(QUEST_SLOT, "offered"));
        
		// Player accepts quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new SetQuestAndModifyKarmaAction(QUEST_SLOT, NEEDED_ITEMS, 5.0),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Klaas has asked me to assist you. I can make a ring that will increase your resistance to poison. I need you to bring me [items]."),
						new DropItemAction("note to apothecary")));
		
		// Player tries to leave without accepting/rejecting the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				"That is not a \"yes\" or \"no\" question. I said, Is that note you are carrying for me?",
				null);
		
		// Player rejects quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				// NPC walks away
				ConversationStates.IDLE,
				"Oh, well, carry on then.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
		
		// Player asks for quest without having Klass's note
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new PlayerHasItemWithHimCondition("note to apothecary")),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"I'm sorry, but I'm much too busy right now. Perhaps you could talk to #Klaas.",
				null);
		
		// Player asks for quest after it is started
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStartedCondition(QUEST_SLOT),
						new QuestNotCompletedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING, 
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I am still waiting for you to bring me [items]."));
		
		// Quest has previously been completed.
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"Thank you so much. It had been so long since I was able to enjoy a fairy cake. Are you enjoying your ring?",
				null);
		
        // Player asks about required items
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("gland", "venom gland", "glands", "venom glands"),
				null,
				ConversationStates.QUESTION_1,
				"Some #snakes have a gland in which their venom is stored.",
				null);
		
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("mandragora", "mandragoras", "root of mandragora", "roots of mandragora", "root of mandragoras", "roots of mandragoras"),
				null,
				ConversationStates.QUESTION_1,
				"This is my favorite of all herbs and one of the most rare. Out past Kalavan there is a hidden path in the trees. At the end you will find what you are looking for.",
				null);
		
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("cake", "fairy cake"),
				null,
				ConversationStates.QUESTION_1,
				"Oh, they are the best treat I have ever tasted. Only the most heavenly creatures could make such angelic food.",
				null);
		
		// Player asks about rings
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("ring", "rings"),
				null,
				ConversationStates.QUESTION_1,
				"There are many types of rings.",
				null);
		
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("medicinal ring", "medicinal rings"),
				null,
				ConversationStates.QUESTION_1,
				"Some poisonous creatures carry them.",
				null);
		
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("antivenom ring", "antivenom rings"),
				null,
				ConversationStates.QUESTION_1,
				"If you bring me what I need I may be able to strengthen a #medicinal #ring.",
				null);
		
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("antitoxin ring", "antitoxin rings", "gm antitoxin ring", "gm antitoxin rings"),
				null,
				ConversationStates.QUESTION_1,
				"Heh! This is the ultimate protection against poisoning. Good luck getting one!",
				null);
		
		// Player asks about snakes
		npc.add(ConversationStates.QUESTION_1,
				Arrays.asList("snake", "snakes", "cobra", "cobras"),
				null,
				ConversationStates.QUESTION_1,
				"I've heard rumor newly discovered put full of snakes somewhere in Ados. But I've never searched for it myself. That kind of work is better left to adventurers.",
				null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Jameson");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()), 
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, 0, "enhancing"))),
				ConversationStates.ATTENDING,
				"Hello again! Did you bring me the #items I requested?",
				null);
		
		// player asks what is missing (says "items")
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("item", "items", "ingredient", "ingredients"),
				null,
				ConversationStates.QUESTION_1,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I need [items]. Did you bring something?"));

		// player says has a required item with him (says "yes")
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.QUESTION_2,
				"What did you bring?",
				null);
		
		// Players says has required items (alternate conversation state)
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.QUESTION_2,
				"What did you bring?",
				null);
		
		// player says does not have a required item with him (says "no")
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Okay. Is there anything else I can help you with?",
				null);
		
		// Players says does not have required items (alternate conversation state)
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Okay. Is there anything else I can help you with?",
				null);
		
		// player says "bye" while listing items
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Goodbye. Let me know when you get the rest of the ingredients.",
				null);
		
		// Returned too early; still working
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestStateStartsWithCondition(QUEST_SLOT, "enhancing;"),
				new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES, "I have not finished with the ring. Please check back in "));
		
		/* player says he didn't bring any items (says no) */
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Ok. Is there anything else I can help you with? Do you need reminded of which #items I still need?", 
				null);

		/* player says he didn't bring any items to different question */
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Ok. Is there anything else I can help you with? Do you need reminded of which #items I still need?",
				null);
		
		// player offers item that isn't in the list.
		npc.add(ConversationStates.QUESTION_2, "",
			new NotCondition(new TriggerInListCondition(NEEDED_ITEMS)),
			ConversationStates.QUESTION_2,
			"I don't believe I asked for that.", null);

		ChatAction enhanceAction = new MultipleActions (
		new SetQuestAction(QUEST_SLOT, "enhancing"),
		new SetQuestToTimeStampAction(QUEST_SLOT, 1),
		new SayTextAction("Thank you. I'll get to work on your new ring right after I enjoy a few of these fairy cakes. Please come back in " + REQUIRED_MINUTES + " minutes.")
		);
		
		/* add triggers for the item names */
		final ItemCollection items = new ItemCollection();
		items.addFromQuestStateString(NEEDED_ITEMS);
		for (final Map.Entry<String, Integer> item : items.entrySet()) {
			npc.add(ConversationStates.QUESTION_2,
					item.getKey(),
					null,
					ConversationStates.QUESTION_2,
					null,
					new CollectRequestedItemsAction(
							item.getKey(),
							QUEST_SLOT,
							"Excellent! Do you have anything else with you?",
							"You brought me that already.",
							enhanceAction,
							ConversationStates.IDLE
							)
			);
		}

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new IncreaseXPAction(2000));
		reward.add(new IncreaseKarmaAction(25.0));
		reward.add(new EquipItemAction("antivenom ring", 1, true));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "enhancing;"),
						new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
			ConversationStates.IDLE, 
			"I have finished enhancing your ring. Now I'll finish the rest of my fairy cakes.", 
			new MultipleActions(reward));
		
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Antivenom Ring",
				"As a favor to an old friend, Jameson the apothecary will strengthen the medicinal ring.",
				false);
		prepareHintNPCs();
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "AntivenomRing";
	}

	public String getTitle() {
		
		return "AntivenomRing";
	}
	
	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}
	
	@Override
	public String getNPCName() {
		return "Jameson";
	}
}
