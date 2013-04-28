/* $Id: AttackStrategyFactoryTest.java,v 1.1 2010/12/02 20:44:08 nhnb Exp $ */
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
package games.stendhal.server.entity.creature.impl.attack;

import static org.junit.Assert.*;

import games.stendhal.server.entity.creature.impl.attack.AttackStrategyFactory;
import games.stendhal.server.entity.creature.impl.attack.AttackWeakest;
import games.stendhal.server.entity.creature.impl.attack.Coward;
import games.stendhal.server.entity.creature.impl.attack.Gandhi;
import games.stendhal.server.entity.creature.impl.attack.HandToHand;
import games.stendhal.server.entity.creature.impl.attack.RangeAttack;
import games.stendhal.server.entity.creature.impl.attack.StupidCoward;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AttackStrategyFactoryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for getArcher.
	 */
	@Test
	public void testGetArcher() {
		final Map<String, String> profiles = new HashMap<String, String>();
		assertTrue(AttackStrategyFactory.get(profiles) instanceof HandToHand);
		profiles.put("archer", null);
		assertTrue(AttackStrategyFactory.get(profiles) instanceof RangeAttack);

	}

	/**
	 * Tests for getGandhi.
	 */
	@Test
	public void testGetGandhi() {
		final Map<String, String> profiles = new HashMap<String, String>();
		assertTrue(AttackStrategyFactory.get(profiles) instanceof HandToHand);
		profiles.put("gandhi", null);
		assertTrue(AttackStrategyFactory.get(profiles) instanceof Gandhi);

	}

	/**
	 * Tests for getCoward.
	 */
	@Test
	public void testGetCoward() {
		final Map<String, String> profiles = new HashMap<String, String>();
		assertTrue(AttackStrategyFactory.get(profiles) instanceof HandToHand);
		profiles.put("coward", null);
		assertTrue(AttackStrategyFactory.get(profiles) instanceof Coward);

	}

	/**
	 * Tests for getStupidCoward.
	 */
	@Test
	public void testGetStupidCoward() {
		final Map<String, String> profiles = new HashMap<String, String>();
		assertTrue(AttackStrategyFactory.get(profiles) instanceof HandToHand);
		profiles.put("stupid coward", null);
		assertTrue(AttackStrategyFactory.get(profiles) instanceof StupidCoward);

	}

	/**
	 * Tests for getting AttackWeakest profile.
	 */
	@Test
	public void testGetAttackWeakest() {
		final Map<String, String> profiles = new HashMap<String, String>();
		assertTrue(AttackStrategyFactory.get(profiles) instanceof HandToHand);
		profiles.put("attack weakest", null);
		assertTrue(AttackStrategyFactory.get(profiles) instanceof AttackWeakest);
	}
}
