package mario;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import client.Client;
import mario.objects.AbstractObject;
import mario.objects.Coin;
import mario.objects.FallingCube;
import mario.objects.Flat;
import mario.objects.Player;
import mario.objects.TubeEntrance;
import mario.objects.TubeExit;
import mario.resources.BackgroundPanel;
import mario.resources.JumpType;
import mario.resources.SelectionPanel;
import network.request.RequestUpdateGameScore;
import objects.GameResult;
import objects.mario.MarioObject;
import objects.mario.MarioType;
import util.Direction;
import util.threadpool.ThreadPool;
import windows.GameSelect;

/**
 * Super Mario map editor and game screen.
 * @author Sahar
 */
public final class SuperMario extends JFrame implements Runnable
{
	private static final long serialVersionUID = 5147600842261285898L;
	private static final Logger LOGGER = Logger.getLogger(SuperMario.class.getName());
	private static final String PACKAGE_PATH = "mario.objects.";
	
	public static final Dimension SCREEN_SIZE = new Dimension(1000, 800);
	public static final int MAX_DISTANCE = SCREEN_SIZE.width + 100;
	public static final int SCREEN_MOVING_POINT = SCREEN_SIZE.width - 450;
	
	protected final JPanel _mapHolder = new JPanel(null);
	protected final SelectionPanel _selectionPanel;
	
	private final BackgroundPanel _background;
	private final List<Component> _addedObjects = new CopyOnWriteArrayList<>();
	private final List<Component> _removedObjects = new CopyOnWriteArrayList<>();
	private final List<TubeExit> _exitTubes = new ArrayList<>();
	
	protected Direction _lastKey = Direction.RIGHT;
	protected Player _player;
	protected boolean _isPlaying;
	
	private int _score;
	private int _fallCount;
	private ScheduledFuture<?> _fallTask;
	
	protected SuperMario()
	{
		super("GameOne Client - Super Mario");
		
		for (final MarioType type : MarioType.values())
			type.initializeImageIcon();
		
		_selectionPanel = new SelectionPanel();
		_background = new BackgroundPanel(_mapHolder);
		
		_mapHolder.setOpaque(false);
		_mapHolder.addMouseMotionListener(new MotionDetector());
		_mapHolder.addMouseListener(new CreateObject());
		
		setLayout(new BorderLayout());
		add(_background, BorderLayout.NORTH);
		add(_selectionPanel, BorderLayout.SOUTH);
		
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addKeyListener(new Movement());
		
		LOGGER.info("SuperMario screen loaded.");
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		// onEnd calls reload.
		if (_isPlaying)
			onEnd();
		else
			reload();
		
		Client.getInstance().setCurrentDetails(GameSelect.getInstance(), null, true);
	}
	
	public void addObject(final String className, final int x, final int y, final boolean soleObject)
	{
		try
		{
			final AbstractObject obj = (AbstractObject) Class.forName(PACKAGE_PATH + className).getConstructors()[0].newInstance(x, y);
			
			addObject(obj, soleObject);
		}
		catch (final SecurityException | ClassNotFoundException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			LOGGER.log(Level.WARNING, "Failed creating SuperMario object: ", e);
		}
	}
	
	public void addObject(final Component obj, final boolean soleObject)
	{
		if (!(obj instanceof AbstractObject))
		{
			LOGGER.warning("Trying to add object: " + obj + " which is not instance of AbstractObject!");
			return;
		}
		
		if (obj instanceof Player)
			_player = (Player) obj;
		else if (obj instanceof TubeExit)
			_exitTubes.add((TubeExit) obj);
		
		_mapHolder.add(obj);
		
		if (soleObject)
		{
			_addedObjects.add(obj);
			
			_mapHolder.repaint();
		}
	}
	
	public void removeObject(final Component obj, final boolean repaint)
	{
		if (!(obj instanceof AbstractObject))
			return;
		
		if (obj instanceof Player)
			_player = null;
		else if (obj instanceof TubeExit)
			_exitTubes.remove(obj);
		
		_mapHolder.remove(obj);
		
		if (_addedObjects.contains(obj))
			_addedObjects.remove(obj);
		else
			_removedObjects.add(obj);
		
		if (repaint)
			_mapHolder.repaint();
	}
	
	public void reset()
	{
		_addedObjects.clear();
		_removedObjects.clear();
		_exitTubes.clear();
		
		_mapHolder.removeAll();
		_mapHolder.revalidate();
		
		for (final MarioObject o : Client.getInstance().getMarioObjects())
			addObject(o.getType().getClassName(), o.getX(), o.getY(), false);
		reload();
	}
	
	public AbstractObject[] getObjects()
	{
		final Component[] components = _mapHolder.getComponents();
		return Arrays.copyOf(components, components.length, AbstractObject[].class);
	}
	
	public void reload()
	{
		_selectionPanel.removeSelectedItem();
		
		_removedObjects.forEach(obj -> addObject(obj, false));
		_addedObjects.forEach(obj -> removeObject(obj, false));
		_removedObjects.clear();
		_addedObjects.clear();
		
		final Collection<Flat> allFlats = getObjectsOfType(Flat.class);
		while (!allFlats.isEmpty())
		{
			final Flat firstFlat = getNearestFlat(0, 0, allFlats);
			final List<Flat> connectedFlats = new LinkedList<>();
			boolean isVertical = false;
			
			allFlats.remove(firstFlat);
			connectedFlats.add(firstFlat);
			
			Flat find;
			while ((find = getNearestFlat(firstFlat.getX(), firstFlat.getY(), allFlats)) != null)
			{
				if (firstFlat.getX() == find.getX())
				{
					connectedFlats.add(find);
					
					if (!isVertical)
						isVertical = true;
				}
				else if (firstFlat.getY() == find.getY())
					connectedFlats.add(find);
				else
					break;
				
				allFlats.remove(find);
			}
			
			firstFlat.setReady(isVertical, connectedFlats);
		}
		
		int maxX = Integer.MIN_VALUE;
		for (final Component c : _mapHolder.getComponents())
			maxX = Math.max(maxX, c.getX());
		
		_mapHolder.setBounds(0, 0, maxX + MAX_DISTANCE, SCREEN_SIZE.height);
		_mapHolder.repaint();
	}
	
	public void onStart()
	{
		reload();
		
		_isPlaying = true;
		
		_selectionPanel.setVisible(false);
		pack();
		
		for (final AbstractObject obj : getObjects())
			obj.onStart();
		
		MarioTaskManager.getInstance().start();
		
		requestFocus();
	}
	
	public void onEnd()
	{
		reload();
		
		_isPlaying = false;
		_fallCount = 0;
		
		_background.changeBackground(MarioType.BACKGROUND.getIcon());
		
		_selectionPanel.setVisible(true);
		pack();
		
		for (final AbstractObject obj : getObjects())
			obj.onEnd();
		
		MarioTaskManager.getInstance().stop();
		
		Client.getInstance().sendPacket(new RequestUpdateGameScore(getObjectsOfType(Coin.class).size() == _score ? GameResult.WIN : GameResult.LOSE, _score));
		
		_score = 0;
	}
	
	public boolean isPlaying()
	{
		return _isPlaying;
	}
	
	public List<Component> getAddedObjects()
	{
		return _addedObjects;
	}
	
	public List<Component> getRemovedObjects()
	{
		return _removedObjects;
	}
	
	public SelectionPanel getSelectionPanel()
	{
		return _selectionPanel;
	}
	
	public JPanel getMapHolder()
	{
		return _mapHolder;
	}
	
	public List<TubeExit> getExitTubes()
	{
		return _exitTubes;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public void increaseScore()
	{
		_score++;
	}
	
	public void changeBackground(final ImageIcon newBackground)
	{
		_background.changeBackground(newBackground);
		
		_fallTask = ThreadPool.scheduleAtFixedRate(this, 0, 2, TimeUnit.SECONDS);
	}
	
	protected Component getNearestObject(final int x, final int y)
	{
		Component nearest = null;
		double dist = Double.MAX_VALUE;
		for (final Component o : _mapHolder.getComponents())
		{
			if (o == _selectionPanel.getSelectedItem())
				continue;
			
			double temp = Math.sqrt(Math.pow(x - o.getX(), 2) + Math.pow(y - o.getY(), 2));
			if (temp < dist)
			{
				nearest = o;
				dist = temp;
			}
		}
		
		return nearest;
	}
	
	private static Flat getNearestFlat(final int x, final int y, final Collection<Flat> searchIn)
	{
		Flat nearest = null;
		double dist = Double.MAX_VALUE;
		for (final Flat o : searchIn)
		{
			double temp = Math.sqrt(Math.pow(x - o.getX(), 2) + Math.pow(y - o.getY(), 2));
			if (temp < dist)
			{
				nearest = o;
				dist = temp;
			}
		}
		
		return nearest;
	}
	
	@SuppressWarnings("unchecked")
	private <A> Collection<A> getObjectsOfType(final Class<A> type)
	{
		final List<A> result = new ArrayList<>();
		for (final Component o : _mapHolder.getComponents())
			if (type.isAssignableFrom(o.getClass()))
				result.add((A) o);
		
		return result;
	}
	
	@Override
	public void run()
	{
		if (_fallCount < 50)
		{
			addObject(new FallingCube(), true);
			
			_fallCount++;
		}
		else
			_fallTask.cancel(false);
	}
	
	protected class MotionDetector extends MouseMotionAdapter
	{
		@Override
		public void mouseMoved(final MouseEvent e)
		{
			if (_isPlaying)
				return;
			if (_selectionPanel.getSelectedItem() == null)
				return;
			
			_selectionPanel.getSelectedItem().setLocation(e.getX(), e.getY());
			
			_mapHolder.repaint();
		}
	}
	
	protected class CreateObject extends MouseAdapter
	{
		@Override
		public void mousePressed(final MouseEvent e)
		{
			if (_isPlaying)
				return;
			
			final boolean remove = e.isControlDown();
			if (_selectionPanel.getSelectedItem() == null && !remove)
				return;
			
			int x = e.getX();
			int y = e.getY();
			
			Component o = null;
			if (remove)
				o = _mapHolder.getComponentAt(x - 1, y - 1);
			else
			{
				// Only coins can float anywhere, anything else should be placed adjacent to something.
				if (MarioType.COIN.getIcon() != _selectionPanel.getSelectedItem().getIcon())
				{
					final int maxX = x + _selectionPanel.getSelectedItem().getWidth();
					final int maxY = y + _selectionPanel.getSelectedItem().getHeight();
					final Component comp = getNearestObject(x, y);
					if (x > comp.getX() && x < comp.getX() + 10 || x < comp.getX() && x > comp.getX() - 10)
						x = comp.getX();
					if (y > comp.getY() && y < comp.getY() + 10 || y < comp.getY() && y > comp.getY() - 10)
						y = comp.getY();
					if (maxX > comp.getX() && maxX < comp.getX() + 10 || maxX < comp.getX() && maxX > comp.getX() - 10)
						x = comp.getX() - _selectionPanel.getSelectedItem().getWidth();
					if (maxY > comp.getY() && maxY < comp.getY() + 10 || maxY < comp.getY() && maxY > comp.getY() - 10)
						y = comp.getY() - _selectionPanel.getSelectedItem().getHeight();
					if (x > comp.getBounds().getMaxX() && x < comp.getBounds().getMaxX() + 10 || x < comp.getBounds().getMaxX() && x > comp.getBounds().getMaxX() - 10)
						x = (int) comp.getBounds().getMaxX();
					if (y > comp.getBounds().getMaxY() && y < comp.getBounds().getMaxY() + 10 || y < comp.getBounds().getMaxY() && y > comp.getBounds().getMaxY() - 10)
						y = (int) comp.getBounds().getMaxY();
				}
				
				o = _selectionPanel.getSelectedItem();
				o.setLocation(x, y);
			}
			
			if (remove)
				removeObject(o, true);
			else
				addObject(o.getName(), o.getX(), o.getY(), true);
		}
	}
	
	protected class Movement extends KeyAdapter
	{
		@Override
		public void keyReleased(final KeyEvent e)
		{
			if (!_isPlaying)
				return;
			
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					_player.setDirection(null);
					break;
			}
		}
		
		@Override
		public void keyPressed(final KeyEvent e)
		{
			if (!_isPlaying)
				return;
			
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_SPACE:
					if (!_player.isJumping() && !_player.getNearbyObjects(_player.getBounds()).get(Direction.BELOW).isEmpty())
						_player.jump(JumpType.JUMP);
					break;
				case KeyEvent.VK_LEFT:
					_lastKey = Direction.LEFT;
					_player.setDirection(Direction.LEFT);
					break;
				case KeyEvent.VK_RIGHT:
					_lastKey = Direction.RIGHT;
					_player.setDirection(Direction.RIGHT);
					break;
				case KeyEvent.VK_DOWN:
					final List<AbstractObject> objects = _player.getNearbyObjects(_player.getBounds()).get(Direction.BELOW);
					if (objects.isEmpty() || !(objects.get(0) instanceof TubeEntrance))
						return;
					
					_player.swallow();
					break;
				case KeyEvent.VK_CONTROL:
					if (_player.canShoot())
						_player.shoot(_lastKey);
					break;
			}
		}
	}
	
	public static SuperMario getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SuperMario INSTANCE = new SuperMario();
	}
}