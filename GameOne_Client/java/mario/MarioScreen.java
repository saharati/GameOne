package mario;

import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import windows.GameSelect;
import mario.gui.BackgroundPanel;
import mario.objects.AbstractObject;
import mario.objects.FallingCube;
import mario.objects.Flat;
import mario.objects.Player;
import mario.objects.TubeEntrance;
import mario.objects.TubeExit;
import mario.prototypes.Direction;
import mario.prototypes.JumpType;
import objects.mario.MarioObject;
import util.threadpool.ThreadPool;
import client.Client;

/**
 * Mario gameplay window.
 * @author Sahar
 */
public final class MarioScreen extends JFrame implements Runnable
{
	private static final long serialVersionUID = -6557768868185141132L;
	
	private static final Logger LOGGER = Logger.getLogger(MarioScreen.class.getName());
	
	// Objects on the map.
	private final List<AbstractObject> _objects = new CopyOnWriteArrayList<>();
	private final List<TubeExit> _exitTubes = new ArrayList<>();
	private Player _player;
	
	// Panels.
	private final JPanel _mapHolder = new JPanel(null);
	private BackgroundPanel _background;
	
	// Player specific.
	private Direction _lastKey = Direction.RIGHT;
	private int _score;
	
	// Falling cubes.
	private ScheduledFuture<?> _fallTask;
	private int _fallCount;
	
	private MarioScreen()
	{
		super("GameOne Client - Super Mario");
		
		SwingUtilities.invokeLater(() ->
		{
			reload();
			
			_mapHolder.setOpaque(false);
			
			_background = new BackgroundPanel(_mapHolder);
			add(_background);
			
			setResizable(false);
			setSize(MarioBuilder.SCREEN_SIZE);
			setLayout(null);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			addKeyListener(new Movement());
			
			LOGGER.info("MarioScreen window loaded.");
		});
	}
	
	public void reload()
	{
		int maxX = 0;
		for (final MarioObject o : Client.getInstance().getMarioObjects())
		{
			try
			{
				final AbstractObject obj = (AbstractObject) Class.forName("mario.objects." + o.getType().getClassName()).getConstructors()[0].newInstance(o.getX(), o.getY());
				if (obj instanceof Player)
					_player = (Player) obj;
				else if (obj instanceof TubeExit)
					_exitTubes.add((TubeExit) obj);
				
				_objects.add(obj);
				
				maxX = Math.max(maxX, obj.getX());
			}
			catch (final SecurityException | ClassNotFoundException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e)
			{
				LOGGER.log(Level.WARNING, "Failed reloading MarioScreen data: ", e);
			}
		}
		
		final Map<Integer, List<Flat>> connectedFlats = new HashMap<>();
		final Collection<Flat> allFlats = getObjectsOfType(Flat.class);
		int horizontalIndex = 1;
		int verticalIndex = -1;
		while (!allFlats.isEmpty())
		{
			final Flat flat = getNearestFlat(0, 0, allFlats);
			allFlats.remove(flat);
			
			Flat find;
			while ((find = getNearestFlat(flat.getX(), flat.getY(), allFlats)) != null)
			{
				if (flat.getX() == find.getX())
				{
					if (!connectedFlats.containsKey(horizontalIndex))
					{
						connectedFlats.put(horizontalIndex, new LinkedList<>());
						connectedFlats.get(horizontalIndex).add(flat);
					}
					
					connectedFlats.get(horizontalIndex).add(find);
				}
				else if (flat.getY() == find.getY())
				{
					if (!connectedFlats.containsKey(verticalIndex))
					{
						connectedFlats.put(verticalIndex, new LinkedList<>());
						connectedFlats.get(verticalIndex).add(flat);
					}
					
					connectedFlats.get(verticalIndex).add(find);
				}
				else
					break;
				
				allFlats.remove(find);
			}
			
			if (connectedFlats.containsKey(horizontalIndex))
				horizontalIndex++;
			else
				verticalIndex--;
		}
		for (final Entry<Integer, List<Flat>> entry : connectedFlats.entrySet())
		{
			entry.getValue().get(0).setReady(entry.getKey() < 0, entry.getValue().get(entry.getValue().size() - 1));
			
			for (final Flat flat : entry.getValue())
				_objects.remove(flat);
			_objects.add(entry.getValue().get(0));
		}
		
		_mapHolder.setBounds(0, 0, maxX + MarioBuilder.MAX_DISTANCE, MarioBuilder.SCREEN_SIZE.height);
		
		_objects.forEach(o -> _mapHolder.add(o));
		_mapHolder.repaint();
	}
	
	public void onStart()
	{
		setVisible(true);
		
		TaskManager.getInstance().start();
	}
	
	public List<AbstractObject> getObjects()
	{
		return _objects;
	}
	
	public List<TubeExit> getExitTubes()
	{
		return _exitTubes;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public JPanel getMapHolder()
	{
		return _mapHolder;
	}
	
	public BackgroundPanel getBg()
	{
		return _background;
	}
	
	public void changeBackground(final Image newBackground)
	{
		_background.changeBackground(newBackground);
		
		_fallTask = ThreadPool.scheduleAtFixedRate(this, 0, 2000);
	}

	public void endGame()
	{
		GameSelect.getInstance().enableAllButtons();
		
		// _client.getConnection().saveUserStatus(Math.max(_score, _client.getScore()), Math.max(_score, _client.getScore()), 0);
	}
	
	public void add(final AbstractObject o)
	{
		_objects.add(o);
		
		_mapHolder.add(o);
		_mapHolder.repaint();
	}
	
	public void remove(final AbstractObject o)
	{
		_objects.remove(o);
		
		_mapHolder.remove(o);
		_mapHolder.repaint();
	}
	
	public void increaseScore()
	{
		_score++;
	}
	
	private Flat getNearestFlat(final int x, final int y, final Collection<Flat> searchIn)
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
		for (final AbstractObject o : _objects)
			if (type.isAssignableFrom(o.getClass()))
				result.add((A) o);
		
		return result;
	}
	
	private class Movement extends KeyAdapter
	{
		@Override
		public void keyReleased(final KeyEvent e)
		{
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
	
	@Override
	public void run()
	{
		if (_fallCount < 50)
		{
			add(new FallingCube());
			
			_fallCount++;
		}
		else
			_fallTask.cancel(false);
	}
	
	public static MarioScreen getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final MarioScreen INSTANCE = new MarioScreen();
	}
}