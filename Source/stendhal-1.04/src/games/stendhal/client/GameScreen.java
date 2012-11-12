/* $Id: GameScreen.java,v 1.216 2012/07/28 10:16:38 kiheru Exp $ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import games.stendhal.client.entity.Corpse;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Item;
import games.stendhal.client.gui.DropTarget;
import games.stendhal.client.gui.GroundContainer;
import games.stendhal.client.gui.j2d.AchievementBoxFactory;
import games.stendhal.client.gui.j2d.RemovableSprite;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.spellcasting.SpellCastingGroundContainerMouseState;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * The game screen. This manages and renders the visual elements of the game.
 */
public class GameScreen extends JComponent implements IGameScreen, DropTarget,
	GameObjects.GameObjectListener, StendhalClient.ZoneChangeListener {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -4070406295913030925L;

	private static Logger logger = Logger.getLogger(GameScreen.class);
	
	/** Map KeyEvents to a number, i.e. to determine position in spells slot based on pressed key **/
	private static final Map<Integer, Integer> keyEventMapping = new HashMap<Integer, Integer>();
	static {
		keyEventMapping.put(KeyEvent.VK_1, Integer.valueOf(1));
		keyEventMapping.put(KeyEvent.VK_2, Integer.valueOf(2));
		keyEventMapping.put(KeyEvent.VK_3, Integer.valueOf(3));
		keyEventMapping.put(KeyEvent.VK_4, Integer.valueOf(4));
		keyEventMapping.put(KeyEvent.VK_5, Integer.valueOf(5));
		keyEventMapping.put(KeyEvent.VK_6, Integer.valueOf(6));
		keyEventMapping.put(KeyEvent.VK_7, Integer.valueOf(7));
		keyEventMapping.put(KeyEvent.VK_8, Integer.valueOf(8));
		keyEventMapping.put(KeyEvent.VK_9, Integer.valueOf(9));
		keyEventMapping.put(KeyEvent.VK_0, Integer.valueOf(10));
	}

	/**
	 * A scale factor for panning delta (to allow non-float precision).
	 */
	protected static final int PAN_SCALE = 8;
	/**
	 * Speed factor for centering the screen. Smaller is faster,
	 * and keeps the player closer to the center of the screen when walking.
	 */
	private static final int PAN_INERTIA = 15;

	private static final Sprite offlineIcon;

	/** the singleton instance. */
	private static GameScreen screen;

	/**
	 * Static game layers.
	 */
	protected StaticGameLayers gameLayers;
	
	/** Entity views container. */
	private final EntityViewManager viewManager = new EntityViewManager();

	/** Actual width of the world in world units. */
	protected int ww;
	/** Actual height of the world in world units. */
	protected int wh;

	/**
	 * The ground layer.
	 */
	private final GroundContainer ground;

	

	/**
	 * The text bubbles.
	 */
	private final List<RemovableSprite> texts;
	
	/**
	 * Text boxes that are anchored to the screen coordinates.
	 */
	private final List<RemovableSprite> staticSprites;

	/**
	 * The text bubbles to remove.
	 */
	private final List<RemovableSprite> textsToRemove;

	
	
	private boolean offline;

	private int blinkOffline;

	/**
	 * The targeted center of view X coordinate (truncated).
	 */
	private int x;

	/**
	 * The targeted center of view Y coordinate (truncated).
	 */
	private int y;

	/** Actual size of the screen in pixels. */
	private int sw;
	private int sh;
	


	/**
	 * The difference between current and target screen view X.
	 */
	private int dvx;

	/**
	 * The difference between current and target screen view Y.
	 */
	private int dvy;

	/**
	 * The current screen view X.
	 */
	private int svx;

	/**
	 * The current screen view Y.
	 */
	private int svy;

	/**
	 * Current panning speed.
	 */
	private int speed;

	static {
		offlineIcon = SpriteStore.get().getSprite("data/gui/offline.png");
	}

	private AchievementBoxFactory achievementBoxFactory;
	
	/**
	 * The canvas can be resized using a split pane. This is 
	 * for adjusting the internal parameters for the change. 
	 */
	private class CanvasResizeListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			Dimension screenSize = stendhal.getScreenSize();
			sw = Math.min(getWidth(), screenSize.width);
			sh = Math.min(getHeight(), screenSize.height);
			// Reset the view so that the player is in the center
			calculateView(x, y);
			center();
		}
	}

	/**
	 * Create a game screen.
	 *
	 * @param client
	 *            The client.
	 */
	public GameScreen(final StendhalClient client) {
		setSize(stendhal.getScreenSize());
		addComponentListener(new CanvasResizeListener());
		
		gameLayers = client.getStaticGameLayers();

		sw = getWidth();
		sh = getHeight();

		x = 0;
		y = 0;
		svx = sw / -2;
		svy = sh / -2;
		dvx = 0;
		dvy = 0;

		speed = 0;

		// Drawing is done in EDT
		texts = Collections.synchronizedList(new LinkedList<RemovableSprite>());
		textsToRemove = Collections.synchronizedList(new LinkedList<RemovableSprite>());
		staticSprites = Collections.synchronizedList(new LinkedList<RemovableSprite>());

		// create ground
		ground = new GroundContainer(client, this, this);

		// register native event handler
		addMouseListener(ground);
		addMouseWheelListener(ground);
		addMouseMotionListener(ground);
		/*
		 * Ignore OS level repaint requests to help systems that create too
		 * many of those. In game DnD is done within AWT so that is not
		 * affected.
		 */
		setIgnoreRepaint(true);
		client.getGameObjects().addGameObjectListener(this);
	}

	/**
	 * Set the default [singleton] screen.
	 *
	 * @param screen
	 *            The screen.
	 */
	
	public static void setDefaultScreen(final GameScreen screen) {
		GameScreen.screen = screen;
	}

	/** @return the GameScreen object. */
	public static GameScreen get() {
		return screen;
	}

	/** @return screen width in world units. */
	private int getViewWidth() {
		return sw / SIZE_UNIT_PIXELS;
	}

	/** @return screen height in world units .*/
	private int getViewHeight() {
		return sh / SIZE_UNIT_PIXELS;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#nextFrame()
	 */
	public void nextFrame() {
		adjustView();
	}
	
	private AchievementBoxFactory getAchievementFactory() {
		if (achievementBoxFactory == null) {
			achievementBoxFactory = new AchievementBoxFactory();
		}
		return achievementBoxFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#addEntity(games.stendhal.client.entity.Entity)
	 */
	public void addEntity(final IEntity entity) {
		EntityView<IEntity> view = viewManager.addEntity(entity);
		if (view != null) {
			if (view instanceof Entity2DView) {
				final Entity2DView<?> inspectable = (Entity2DView<?>) view;
				
				inspectable.setInspector(ground);
			}
			if (entity.isUser()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						center();
					}
				});
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#removeEntity(games.stendhal.client.entity.Entity)
	 */
	public void removeEntity(final IEntity entity) {
		viewManager.removeEntity(entity);
	}

	/**
	 * Update the view position to center the target position.
	 */
	private void adjustView() {
		/*
		 * Already centered?
		 */
		if ((dvx == 0) && (dvy == 0)) {
			return;
		}

		final int sx = convertWorldXToScreenView(x) + (SIZE_UNIT_PIXELS / 2);
		final int sy = convertWorldYToScreenView(y) + (SIZE_UNIT_PIXELS / 2);

		if ((sx < 0) || (sx >= sw) || (sy < -SIZE_UNIT_PIXELS) || (sy > sh)) {
			/*
			 * If off screen, just center
			 */
			center();
		} else {
			/*
			 * Calculate the target speed. The farther away, the faster.
			 */
			final int dux = dvx / PAN_INERTIA;
			final int duy = dvy / PAN_INERTIA;

			final int tspeed = ((dux * dux) + (duy * duy)) * PAN_SCALE;

			if (speed > tspeed) {
				speed = (speed + speed + tspeed) / 3;

				/*
				 * Don't stall
				 */
				if ((dvx != 0) || (dvy != 0)) {
					speed = Math.max(speed, 1);
				}
			} else if (speed < tspeed) {
				speed += 2;
			}

			/*
			 * Moving?
			 */
			if (speed != 0) {
				/*
				 * Not a^2 + b^2 = c^2, but good enough
				 */
				final int scalediv = (Math.abs(dvx) + Math.abs(dvy)) * PAN_SCALE;

				int dx = speed * dvx / scalediv;
				int dy = speed * dvy / scalediv;

				/*
				 * Don't overshoot. Don't stall.
				 */
				if (dvx < 0) {
					dx = Math.max(Math.min(-1, dx), dvx);
				} else if (dvx > 0) {
					dx = Math.min(Math.max(1, dx), dvx);
				}

				if (dvy < 0) {
					dy = Math.max(Math.min(-1, dy), dvy);
				} else if (dvy > 0) {
					dy = Math.min(Math.max(1, dy), dvy);
				}

				/*
				 * Adjust view
				 */
				svx += dx;
				dvx -= dx;

				svy += dy;
				dvy -= dy;
			}
		}
	}

	/**
	 * Updates the target position of the view center.
	 * Prefer top left if the map is smaller than the screen.
	 * 
	 * @param x preferred x of center, if the map is large enough
	 * @param y preferred y of center, if the map is large enough
	 */
	private void calculateView(int x, int y) {
		// Coordinates for a screen centered on player
		int cvx = (x * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sw / 2);
		int cvy = (y * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sh / 2);

		/*
		 * Keep the world within the screen view
		 */
		final int maxX = ww * SIZE_UNIT_PIXELS - sw;
		cvx = Math.min(cvx, maxX);
		cvx = Math.max(cvx, 0);
		
		final int maxY = wh * SIZE_UNIT_PIXELS - sh;
		cvy = Math.min(cvy, maxY);
		cvy = Math.max(cvy, 0);
		
		// Differences from center
		dvx = cvx - svx;
		dvy = cvy - svy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#center()
	 */
	public void center() {
		svx += dvx;
		svy += dvy;

		dvx = 0;
		dvy = 0;

		speed = 0;
	}
	
	@Override
	public void paintImmediately(int x, int y, int w, int h) {
		/*
		 * Try to keep the old screen while the user is switching maps.
		 * 
		 * NOTE: Relies on the repaint() requests to eventually come to this,
		 * so if swing internals change some time in the future, a new solution
		 * may be needed.
		 */
		if (StendhalClient.get().tryAcquireDrawingSemaphore()) {
			try {
				super.paintImmediately(x, y, w, h);
			} finally {
				StendhalClient.get().releaseDrawingSemaphore();
			}
		}
	}
	
	@Override
	public void paintComponent(final Graphics g) {
		viewManager.sort();
		Graphics2D g2d = (Graphics2D) g;

		// Draw the GameLayers from bottom to top, relies on exact naming of the
		// layers
		final String set = gameLayers.getAreaName();
		
		// An adjusted graphics object so that the drawn objects do not need to
		// know about converting the position to screen
		Graphics2D graphics = (Graphics2D) g2d.create();
		if (graphics.getClipBounds() == null) {
			Dimension screenSize = stendhal.getScreenSize();
			graphics.setClip(0, 0, Math.min(getWidth(), screenSize.width),
					Math.min(getHeight(), screenSize.height));
		}

		int xAdjust = -getScreenViewX();
		int yAdjust = -getScreenViewY();
		graphics.translate(xAdjust, yAdjust);
		// End of the world (map falls short of the view)?
		if (xAdjust > 0) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, xAdjust, sh);
		}
		
		if (yAdjust > 0) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, sw, yAdjust);
		}
		
		int tmpY = yAdjust + convertWorldToScreen(wh);
		if (tmpY < sh) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, tmpY, sw, sh);
		}
		
		int tmpX = yAdjust + convertWorldToScreen(ww);
		if (tmpX < sw) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(tmpX, 0, sw, sh);
		}
		
		int startTileX = Math.max(0, (int) getViewX());
		int startTileY = Math.max(0, (int) getViewY());
		
		// Restrict the drawn area by the clip bounds. Smaller than gamescreen
		// draw requests can come for example from dragging items
		Rectangle clip = graphics.getClipBounds();
		startTileX = Math.max(startTileX, clip.x / IGameScreen.SIZE_UNIT_PIXELS);
		startTileY = Math.max(startTileY, clip.y / IGameScreen.SIZE_UNIT_PIXELS);
		int layerWidth = getViewWidth();
		int layerHeight = getViewHeight();
		// +2 is needed to ensure the drawn area is covered by the tiles
		layerWidth = Math.min(layerWidth, clip.width / IGameScreen.SIZE_UNIT_PIXELS) + 2;
		layerHeight = Math.min(layerHeight, clip.height / IGameScreen.SIZE_UNIT_PIXELS) + 2;
		
		gameLayers.drawLayers(graphics, set, "floor_bundle", startTileX, 
				startTileY, layerWidth, layerHeight, "blend_ground", "0_floor",
				"1_terrain", "2_object");
		
		viewManager.draw(graphics);

		gameLayers.drawLayers(graphics, set, "roof_bundle", startTileX,
				startTileY, layerWidth, layerHeight, "blend_roof", "3_roof",
				"4_roof_add");
		
		// Draw the top portion screen entities (such as HP/title bars).
		viewManager.drawTop(graphics);
		
		drawText(g2d);

		// Offline
		if (offline && (blinkOffline > 0)) {
			offlineIcon.draw(g2d, 560, 420);
		}

		if (blinkOffline < -10) {
			blinkOffline = 20;
		} else {
			blinkOffline--;
		}

		graphics.dispose();
	}

	/**
	 * Draw the screen text bubbles.
	 * 
	 * @param g2d destination graphics
	 */
	private void drawText(final Graphics2D g2d) {
		texts.removeAll(textsToRemove);
		staticSprites.removeAll(textsToRemove);
		textsToRemove.clear();
		
		/*
		 * Text objects know their original placement relative to the screen,
		 * not to the map. Pass them a shifted coordinate system. 
		 */
		g2d.translate(-getScreenViewX(), -getScreenViewY());
		
		synchronized (texts) {
			for (final RemovableSprite text : texts) {
				if (!text.shouldBeRemoved()) {
					text.draw(g2d);
				} else {
					removeText(text);
				}
			}
		}
		
		// Restore the coordinates
		g2d.translate(getScreenViewX(), getScreenViewY());
		// These are anchored to the screen, so they can use the usual proper
		// coordinates.
		synchronized (staticSprites) {
			for (final RemovableSprite text : staticSprites) {
				if (!text.shouldBeRemoved()) {
					text.draw(g2d);
				} else {
					removeText(text);
				}
			}
		}
	}

	/**
	 * Get the view X world coordinate.
	 *
	 * @return The X coordinate of the left side.
	 */
	private double getViewX() {
		return (double) getScreenViewX() / SIZE_UNIT_PIXELS;
	}

	/**
	 * Get the view Y world coordinate.
	 *
	 * @return The Y coordinate of the left side.
	 */
	private double getViewY() {
		return (double) getScreenViewY() / SIZE_UNIT_PIXELS;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#setMaxWorldSize(double, double)
	 */
	public void setMaxWorldSize(final double width, final double height) {
		ww = (int) width;
		wh = (int) height;
		calculateView(x, y);
		center();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#setOffline(boolean)
	 */
	public void setOffline(final boolean offline) {
		this.offline = offline;
	}
	
	/**
	 * Adds a text bubble at a give position.
	 * 
	 * @param sprite 
	 * @param x 
	 * @param y
	 * @param textLength 
	 */
	public void addTextBox(Sprite sprite, double x, double y, int textLength) {
		int sx = convertWorldToScreen(x);
		int sy = convertWorldToScreen(y);
		// Point alignment: left, bottom
		sy -= sprite.getHeight();

		sx = keepSpriteOnMapX(sprite, sx);
		sy = keepSpriteOnMapY(sprite, sy);
		
		/*
		 * Adjust the position of boxes placed at the same point to make it
		 * clear for the player there are more than one.
		 */
		boolean found = true;
		int tries = 0;

		while (found) {
			found = false;

			synchronized (texts) {
				for (final RemovableSprite item : texts) {
					if ((item.getX() == sx) && (item.getY() == sy)) {
						found = true;
						sy += (SIZE_UNIT_PIXELS / 2);
						sy = keepSpriteOnMapY(sprite, sy);
						break;
					}
				}
			}
			
			tries++;
			// give up, if no location found in a reasonable amount of tries
			if (tries > 20) {
				break;
			}
		}

		texts.add(new RemovableSprite(sprite, sx, sy, Math.max(
				RemovableSprite.STANDARD_PERSISTENCE_TIME, textLength
						* RemovableSprite.STANDARD_PERSISTENCE_TIME / 50)));
	}

	/**
	 * Try to keep a sprite on the map. Adjust the Y coordinate.
	 * 
	 * @param sprite
	 * @param sy suggested Y coordinate on screen
	 * @return new Y coordinate
	 */
	private int keepSpriteOnMapY(Sprite sprite, int sy) {
		sy = Math.max(sy, 0);
		/*
		 * Allow placing beyond the map, but only if the area is on the screen.
		 * Do not try to adjust the coordinates if the world size is not known
		 * yet (as in immediately after a zone change)
		 */
		if (wh != 0) {
			sy = Math.min(sy, Math.max(getHeight() + svy,
					convertWorldToScreen(wh)) - sprite.getHeight());
		}
		return sy;
	}

	/**
	 * Try to keep a sprite on the map. Adjust the X coordinate.
	 * 
	 * @param sprite
	 * @param sx suggested X coordinate on screen
	 * @return new X coordinate
	 */
	private int keepSpriteOnMapX(Sprite sprite, int sx) {
		sx = Math.max(sx, 0);
		/*
		 * Allow placing beyond the map, but only if the area is on the screen.
		 * Do not try to adjust the coordinates if the world size is not known
		 * yet (as in immediately after a zone change)
		 */
		if (ww != 0) {
			sx = Math.min(sx, Math.max(getWidth() + svx, convertWorldToScreen(ww)) - sprite.getWidth());
		}
		return sx;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#removeText(games.stendhal.client.gui.j2d.Text)
	 */
	public void removeText(final RemovableSprite entity) {
		textsToRemove.add(entity);
	}

	/**
	 * Remove all map objects.
	 */
	private void removeAllObjects() {
		logger.debug("CLEANING screen object list");
		texts.clear();
		textsToRemove.clear();
		// staticSprites contents are not zone specific, so don't clear those
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#clearTexts()
	 */
	public void clearTexts() {
		synchronized (texts) {
			for (final RemovableSprite text : texts) {
				textsToRemove.add(text);
			}
		}
		synchronized (staticSprites) {
			for (final RemovableSprite text : staticSprites) {
				textsToRemove.add(text);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getEntityViewAt(double, double)
	 */
	public EntityView<?> getEntityViewAt(final double x, final double y) {
		final int sx = convertWorldToScreen(x);
		final int sy = convertWorldToScreen(y);
		return viewManager.getEntityViewAt(x, y, sx, sy);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getMovableEntityViewAt(double,
	 *      double)
	 */
	public EntityView<?> getMovableEntityViewAt(final double x, final double y) {
		final int sx = convertWorldToScreen(x);
		final int sy = convertWorldToScreen(y);
		return viewManager.getMovableEntityViewAt(x, y, sx, sy);
	}

	/*
	 * (non-Javadoc)
	 * @see games.stendhal.client.IGameScreen#getTextAt(int, int)
	 */
	public RemovableSprite getTextAt(final int x, final int y) {
		// staticTexts are drawn on top of the others; those in the end of the
		// lists are above preceding texts
		synchronized (staticSprites) {
			final ListIterator<RemovableSprite> it = staticSprites.listIterator(staticSprites.size());

			while (it.hasPrevious()) {
				final RemovableSprite text = it.previous();

				if (text.getArea().contains(x, y)) {
					return text;
				}
			}
		}
		// map pixel coordinates
		final int tx = x + svx;
		final int ty = y + svy;
		synchronized (texts) {
			final ListIterator<RemovableSprite> it = texts.listIterator(texts.size());

			while (it.hasPrevious()) {
				final RemovableSprite text = it.previous();

				if (text.getArea().contains(tx, ty)) {
					return text;
				}
			}
		}

		return null;
	}

	/**
	 * Convert world X coordinate to screen view coordinate.
	 *
	 * @param wx
	 *            World X coordinate.
	 *
	 * @return Screen X coordinate (in integer value).
	 */
	private int convertWorldXToScreenView(final double wx) {
		return convertWorldToScreen(wx) - svx;
	}

	/**
	 * Convert world Y coordinate to screen view coordinate.
	 *
	 * @param wy
	 *            World Y coordinate.
	 *
	 * @return Screen Y coordinate (in integer value).
	 */
	private int convertWorldYToScreenView(final double wy) {
		return convertWorldToScreen(wy) - svy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertWorldToScreenView(double,
	 *      double)
	 */
	public Point convertWorldToScreenView(final double wx, final double wy) {
		return new Point(convertWorldXToScreenView(wx),
				convertWorldYToScreenView(wy));
	}

	/**
	 * Convert a world unit value to a screen unit value.
	 *
	 * @param w World value.
	 *
	 * @return A screen value (in pixels).
	 */
	private int convertWorldToScreen(final double w) {
		return (int) (w * SIZE_UNIT_PIXELS);
	}

	/**
	 * Convert screen coordinates to world coordinates.
	 *
	 * @param x
	 *            The virtual screen X coordinate.
	 * @param y
	 *            The virtual screen Y coordinate.
	 *
	 * @return World coordinates.
	 */
	private Point2D convertScreenToWorld(final int x, final int y) {
		return new Point.Double((double) x / SIZE_UNIT_PIXELS, (double) y
				/ SIZE_UNIT_PIXELS);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertScreenViewToWorld(java.awt.Point)
	 */
	public Point2D convertScreenViewToWorld(final Point p) {
		return convertScreenViewToWorld(p.x, p.y);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertScreenViewToWorld(int, int)
	 */
	public Point2D convertScreenViewToWorld(final int x, final int y) {
		return convertScreenToWorld(x + getScreenViewX(), y + getScreenViewY());
	}

	/**
	 * Get the view X screen coordinate.
	 *
	 * @return The X coordinate of the left side.
	 */
	private int getScreenViewX() {
		return svx;
	}

	/**
	 * Get the view Y screen coordinate.
	 *
	 * @return The Y coordinate of the left side.
	 */
	private int getScreenViewY() {
		return svy;
	}

	//
	// PositionChangeListener
	//

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#positionChanged(double, double)
	 */
	public void positionChanged(final double x, final double y) {
		final int ix = (int) x;
		final int iy = (int) y;

		/*
		 * Save CPU cycles
		 */
		if ((ix != this.x) || (iy != this.y)) {
			this.x = ix;
			this.y = iy;

			calculateView(ix, iy);
		}
	}
	
	public void dropEntity(IEntity entity, int amount, Point point) {
		// Just pass it to the ground container
		ground.dropEntity(entity, amount, point);
	}

	/**
	 * Draw a box for a reached achievement with given title, description and category
	 * 
	 * @param title
	 * @param description
	 * @param category
	 */
	public void addAchievementBox(String title, String description,
			String category) {
		final Sprite sprite = getAchievementFactory().createAchievementBox(title, description, category);
		
		/*
		 * Keep the achievements a bit longer on the screen. They do not leave
		 * a line to the chat log, so we give the player a bit more time to
		 * admire her prowess.
		 */
		addStaticSprite(sprite, 2 * RemovableSprite.STANDARD_PERSISTENCE_TIME, 0);
	}
	
	/**
	 * Add a text box bound to the bottom of the screen, with a timeout
	 * dependent on the text length.
	 * 
	 * @param sprite
	 * @param textLength
	 * @param priority importance of the message to keep it above others
	 */
	public void addStaticText(Sprite sprite, int textLength, int priority) {
		addStaticSprite(sprite,
				Math.max(RemovableSprite.STANDARD_PERSISTENCE_TIME,
				textLength * RemovableSprite.STANDARD_PERSISTENCE_TIME / 50),
				priority);
	}
	
	/**
	 * Add a sprite anchored to the screen bottom.
	 * 
	 * @param sprite sprite
	 * @param persistTime time to stay on the screen before being automatically
	 * 	removed
	 *  @param priority importance of the message to keep it above others
	 */
	private void addStaticSprite(Sprite sprite, long persistTime, int priority) {
		int x = (getWidth() - sprite.getWidth()) / 2;
		int y = getHeight() - sprite.getHeight();
		RemovableSprite msg = new RemovableSprite(sprite, x, y, persistTime);
		msg.setPriority(priority);
		staticSprites.add(msg);
		Collections.sort(staticSprites);
	}
	
	public void onZoneUpdate() {
		viewManager.resetViews();
	}

	public void onZoneChange() {
		removeAllObjects();
	}

	/**
	 * Switch to spell casting triggered by a key event
	 * @param e triggering key event
	 */
	public void switchToSpellCasting(KeyEvent e) {
		RPObject spell = findSpell(e);
		// only if a spell was found switch to spell casting
		// i.e. Ctrl + 9 was issued, but player only has 8 spell would return null
		if(spell != null) {
			switchToSpellCastingState(spell);
		}
	}

	/**
	 * Switch to spell casting with an already chosen spell
	 * 
	 * @param spell the chosen spell
	 */
	public void switchToSpellCastingState(RPObject spell) {
		SpellCastingGroundContainerMouseState newState = new SpellCastingGroundContainerMouseState(this.ground);
		this.ground.setNewMouseHandlerState(newState);
		newState.setSpell(spell);
	}
	
	private RPObject findSpell(KeyEvent e) {
		RPObject player = StendhalClient.get().getPlayer();
		Integer position = keyEventMapping.get(e.getKeyCode());
		RPSlot slot = player.getSlot("spells");
		Integer counter = Integer.valueOf(1);
		for (RPObject spell : slot) {
			if(counter.equals(position)) {
				return spell;
			}
			counter = Integer.valueOf(counter.intValue() + 1);
		}
		return null;
	}

	public boolean canAccept(IEntity entity) {
		if ((entity instanceof Item) || (entity instanceof Corpse)) {
			return true;
		}
		return false;
	}
}
