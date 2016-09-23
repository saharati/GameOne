package pacman.objects;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import objects.pacman.PacmanObject;
import pacman.MapBuilder;
import pacman.objects.MapObject;
import util.random.Rnd;
import util.threadpool.ThreadPool;

/**
 * A map level in pacman.
 * @author Sahar
 */
public final class PacmanMap extends JFrame
{
	private static final long serialVersionUID = -9081629023961869616L;
	
	private static final List<Character> DIRECTIONS = new ArrayList<>();
	static
	{
		DIRECTIONS.add('w');
		DIRECTIONS.add('a');
		DIRECTIONS.add('s');
		DIRECTIONS.add('d');
	}
	
	private final ScheduledFuture<?>[] _schedules = new ScheduledFuture<?>[3];
	private final Map<MapObject, Character> _mobs = new ConcurrentHashMap<>();
	private final char[] _nextMoves = new char[2];
	private final MapObject[][] _objects;
	private final int _totalStars;
	
	private MapObject _player;
	private boolean _slow;
	private int _slowTime;
	
	public PacmanMap(final MapObject[][] objects)
	{
		super("GameOne Client - Pacman");
		
		_objects = objects;
		_totalStars = getStars().size();
		
		for (int i = 0;i < _objects.length;i++)
		{
			for (int j = 0;j < _objects[i].length;j++)
			{
				if (_objects[i][j].getType().isPlayer())
					_player = _objects[i][j];
				else if (_objects[i][j].getType().isMonster())
					_mobs.put(_objects[i][j], '0');
				
				add(_objects[i][j]);
			}
		}
		
		int pos = 0;
		getContentPane().setComponentZOrder(_player, pos++);
		for (final MapObject mob : _mobs.keySet())
			getContentPane().setComponentZOrder(mob, pos++);
		
		getContentPane().setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1030, 795);
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		addKeyListener(new Movement());
	}
	
	public MapObject[][] getObjects()
	{
		return _objects;
	}
	
	public void reset()
	{
		_nextMoves[0] = 0;
		_nextMoves[1] = 0;
		
		_player.reset();
		_mobs.keySet().forEach(m -> m.reset());
		for (int i = 0;i < _objects.length;i++)
			for (int j = 0;j < _objects[i].length;j++)
				_objects[i][j].reset();
		
		_slow = false;
		_slowTime = 0;
	}
	
	private List<MapObject> getStars()
	{
		final List<MapObject> stars = new ArrayList<>();
		for (int i = 0;i < _objects.length;i++)
			for (int j = 0;j < _objects[i].length;j++)
				if (_objects[i][j].getType().isStar())
					stars.add(_objects[i][j]);
		
		return stars;
	}
	
	private MapObject getObjectAt(final int x, final int y)
	{
		for (int i = 0;i < _objects.length;i++)
			for (int j = 0;j < _objects[i].length;j++)
				if (isWithin(x, y, _objects[i][j].getX(), _objects[i][j].getY()))
					return _objects[i][j];
		
		return MapObject.EMPTY;
	}
	
	private boolean isWithin(final int x, final int y, final int objX, final int objY)
	{
		return x > objX && x < objX + 64 && y > objY && y < objY + 64;
	}
	
	private void checkForDirectionChange(final int x, final int y)
	{
		if (_nextMoves[1] == 0)
			return;
		
		switch (_nextMoves[1])
		{
			case 'w':
				if ((x % 64 == 0 && y % 64 == 0 || _nextMoves[0] == 's') && !getObjectAt(x + 1, y - 1 % 64 == 0 ? y - 2 : y - 1).getType().isWall() && !getObjectAt(x + 63, y - 1 % 64 == 0 ? y - 2 : y - 1).getType().isWall())
				{
					_nextMoves[1] = 0;
					_nextMoves[0] = 'w';
				}
				break;
			case 's':
				if ((x % 64 == 0 && y % 64 == 0 || _nextMoves[0] == 'w') && !getObjectAt(x + 1, y + 65 % 64 == 0 ? y + 66 : y + 65).getType().isWall() && !getObjectAt(x + 63, y + 65 % 64 == 0 ? y + 66 : y + 65).getType().isWall())
				{
					_nextMoves[1] = 0;
					_nextMoves[0] = 's';
				}
				break;
			case 'a':
				if ((x % 64 == 0 && y % 64 == 0 || _nextMoves[0] == 'd') && !getObjectAt(x - 1 % 64 == 0 ? x - 2 : x - 1, y + 1).getType().isWall() && !getObjectAt(x - 1 % 64 == 0 ? x - 2 : x - 1, y + 63).getType().isWall())
				{
					_nextMoves[1] = 0;
					_nextMoves[0] = 'a';
				}
				break;
			case 'd':
				if ((x % 64 == 0 && y % 64 == 0 || _nextMoves[0] == 'a') && !getObjectAt(x + 65 % 64 == 0 ? x + 66 : x + 65, y + 1).getType().isWall() && !getObjectAt(x + 65 % 64 == 0 ? x + 66 : x + 65, y + 63).getType().isWall())
				{
					_nextMoves[1] = 0;
					_nextMoves[0] = 'd';
				}
				break;
		}
	}
	
	private char getRandomDirectionChange(final int x, final int y, final char direction)
	{
		if (x % 64 != 0 || y % 64 != 0 || Rnd.nextBoolean())
			return direction;
		
		switch (direction)
		{
			case 'w':
			case 's':
				final boolean canGoLeft = !getObjectAt(x - 1 % 64 == 0 ? x - 2 : x - 1, y + 1).getType().isWall() && !getObjectAt(x - 1 % 64 == 0 ? x - 2 : x - 1, y + 63).getType().isWall();
				final boolean canGoRight = !getObjectAt(x + 65 % 64 == 0 ? x + 66 : x + 65, y + 1).getType().isWall() && !getObjectAt(x + 65 % 64 == 0 ? x + 66 : x + 65, y + 63).getType().isWall();
				if (canGoLeft && !canGoRight || canGoLeft && canGoRight && Rnd.nextBoolean())
					return 'a';
				if (canGoRight)
					return 'd';
				break;
			case 'a':
			case 'd':
				final boolean canGoUp = !getObjectAt(x + 1, y - 1 % 64 == 0 ? y - 2 : y - 1).getType().isWall() && !getObjectAt(x + 63, y - 1 % 64 == 0 ? y - 2 : y - 1).getType().isWall();
				final boolean canGoDown = !getObjectAt(x + 1, y + 65 % 64 == 0 ? y + 66 : y + 65).getType().isWall() && !getObjectAt(x + 63, y + 65 % 64 == 0 ? y + 66 : y + 65).getType().isWall();
				if (canGoUp && !canGoDown || canGoUp && canGoDown && Rnd.nextBoolean())
					return 'w';
				if (canGoDown)
					return 's';
				break;
		}
		
		return direction;
	}
	
	private void setSlow()
	{
		_slowTime = 1500;
		_slow = true;
	}
	
	private MapObject getTarget(final MapObject target1, final MapObject target2)
	{
		if (target1.getType().isWall())
			return target1;
		if (target2.getType().isWall())
			return target2;
		if (target1.getType().isMonster())
			return target1;
		if (target2.getType().isMonster())
			return target2;
		if (target1.getType().isFood() || target1.getType().isStar())
			return target1;
		if (target2.getType().isFood() || target2.getType().isStar())
			return target2;
		
		return target1;
	}
	
	private class Movement extends KeyAdapter
	{
		@Override
		public void keyPressed(final KeyEvent e)
		{
			char ch = 0;
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					ch = 'w';
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					ch = 'a';
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					ch = 'd';
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					ch = 's';
					break;
				default:
					return;
			}
			
			if (_nextMoves[0] == 0)
			{
				_nextMoves[0] = ch;
				
				_schedules[0] = ThreadPool.scheduleAtFixedRate(new Animation(), 500, 500);
				_schedules[1] = ThreadPool.scheduleAtFixedRate(new PlayerMove(), 3, 3);
				_schedules[2] = ThreadPool.scheduleAtFixedRate(new MobMove(), 3, 3);
			}
			else if (_nextMoves[0] == ch)
				_nextMoves[1] = 0;
			else
				_nextMoves[1] = ch;
		}
	}

	private class Animation implements Runnable
	{
		@Override
		public void run()
		{
			if (_player.getType() == PacmanObject.PLAYER_NORMAL)
				_player.setType(PacmanObject.getObjectForDirection(_nextMoves[0]));
			else
				_player.setType(PacmanObject.PLAYER_NORMAL);
		}
	}

	private class PlayerMove implements Runnable
	{
		@Override
		public void run()
		{
			int toX = _player.getX();
			int toY = _player.getY();
			checkForDirectionChange(toX, toY);
			
			MapObject finalTarget = null;
			switch (_nextMoves[0])
			{
				case 'a':
					finalTarget = getTarget(getObjectAt(toX - 1 % 64 == 0 ? toX - 2 : toX - 1, toY + 1), getObjectAt(toX - 1 % 64 == 0 ? toX - 2 : toX - 1, toY + 63));
					toX--;
					break;
				case 'w':
					finalTarget = getTarget(getObjectAt(toX + 1, toY - 1 % 64 == 0 ? toY - 2 : toY - 1), getObjectAt(toX + 63, toY - 1 % 64 == 0 ? toY - 2 : toY - 1));
					toY--;
					break;
				case 's':
					finalTarget = getTarget(getObjectAt(toX + 1, toY + 65 % 64 == 0 ? toY + 66 : toY + 65), getObjectAt(toX + 63, toY + 65 % 64 == 0 ? toY + 66 : toY + 65));
					toY++;
					break;
				case 'd':
					finalTarget = getTarget(getObjectAt(toX + 65 % 64 == 0 ? toX + 66 : toX + 65, toY + 1), getObjectAt(toX + 65 % 64 == 0 ? toX + 66 : toX + 65, toY + 63));
					toX++;
					break;
			}
			
			if (toX > -63 && toX < 1023 && toY > -63 && toY < 767)
			{
				if (!finalTarget.getType().isWall())
				{
					if (finalTarget.getType().isStar())
					{
						finalTarget.setType(PacmanObject.EMPTY);
						
						if (getStars().isEmpty())
						{
							for (final ScheduledFuture<?> future : _schedules)
								future.cancel(false);
							
							MapBuilder.getInstance().addScore(_totalStars);
							
							JOptionPane.showMessageDialog(null, "You won!", "Pazam!", JOptionPane.INFORMATION_MESSAGE);
							setVisible(false);
							
							final PacmanMap next = MapBuilder.getInstance().getNextMap();
							if (next != null)
								next.setVisible(true);
							else
								MapBuilder.getInstance().reset();
						}
					}
					else if (finalTarget.getType().isFood())
					{
						finalTarget.setType(PacmanObject.EMPTY);
						
						setSlow();
					}
					else if (finalTarget.getType().isMonster())
					{
						if (_slow)
							finalTarget.setType(PacmanObject.EMPTY);
						else
						{
							for (final ScheduledFuture<?> future : _schedules)
								future.cancel(false);
							
							MapBuilder.getInstance().addScore(_totalStars);
							
							JOptionPane.showMessageDialog(null, "You lost!", "Noob", JOptionPane.INFORMATION_MESSAGE);
							setVisible(false);
							MapBuilder.getInstance().reset();
						}
					}
					
					_player.setLocation(toX, toY);
					if (_player.getType() != PacmanObject.PLAYER_NORMAL)
						_player.setType(PacmanObject.getObjectForDirection(_nextMoves[0]));
				}
			}
			else
			{
				if (toX == -64)
					toX = 1023;
				else if (toX == 1024)
					toX = -63;
				else if (toY == -64)
					toY = 767;
				else if (toY == 768)
					toY = -63;
				
				_player.setLocation(toX, toY);
				if (_player.getType() != PacmanObject.PLAYER_NORMAL)
					_player.setType(PacmanObject.getObjectForDirection(_nextMoves[0]));
			}
		}
	}
	
	private class MobMove implements Runnable
	{
		private boolean _slowed;
		
		@Override
		public void run()
		{
			if (_slow)
			{
				// In order to move the mobs slower, activate the thread every 6ms instead of every 3ms.
				_slowed = !_slowed;
				if (!_slowed)
					return;
				
				// Start the flashing animation when remaining time < 501.
				if (_slowTime < 501 && _slowTime % 50 == 0)
				{
					for (final Entry<MapObject, Character> entry : _mobs.entrySet())
					{
						if (entry.getKey().getType() == PacmanObject.EMPTY)
							continue;
						
						if (entry.getKey().getType() == PacmanObject.MOB_SLOW)
							entry.getKey().setType(entry.getKey().getReservedType());
						else
							entry.getKey().setType(PacmanObject.MOB_SLOW);
					}
				}
				
				_slow = --_slowTime != 0;
			}
			
			MapObject finalTarget = null;
			for (final Entry<MapObject, Character> entry : _mobs.entrySet())
			{
				if (entry.getKey().getType() == PacmanObject.EMPTY)
					continue;
				
				if (_slowTime > 500)
				{
					if (_slow)
					{
						if (entry.getKey().getType() != PacmanObject.MOB_SLOW)
							entry.getKey().setType(PacmanObject.MOB_SLOW);
					}
					else if (entry.getKey().getType() == PacmanObject.MOB_SLOW)
						entry.getKey().setType(entry.getKey().getReservedType());
				}
				else if (_slowTime == 0 && entry.getKey().getType() == PacmanObject.MOB_SLOW)
					entry.getKey().setType(entry.getKey().getReservedType());
				
				int toX = entry.getKey().getX();
				int toY = entry.getKey().getY();
				entry.setValue(getRandomDirectionChange(toX, toY, entry.getValue()));
				if (entry.getValue() == '0')
					entry.setValue(DIRECTIONS.get(Rnd.get(DIRECTIONS.size())));
				
				switch (entry.getValue())
				{
					case 'a':
						finalTarget = getTarget(getObjectAt(toX - 1 % 64 == 0 ? toX - 2 : toX - 1, toY + 1), getObjectAt(toX - 1 % 64 == 0 ? toX - 2 : toX - 1, toY + 63));
						toX--;
						break;
					case 'w':
						finalTarget = getTarget(getObjectAt(toX + 1, toY - 1 % 64 == 0 ? toY - 2 : toY - 1), getObjectAt(toX + 63, toY - 1 % 64 == 0 ? toY - 2 : toY - 1));
						toY--;
						break;
					case 's':
						finalTarget = getTarget(getObjectAt(toX + 1, toY + 65 % 64 == 0 ? toY + 66 : toY + 65), getObjectAt(toX + 63, toY + 65 % 64 == 0 ? toY + 66 : toY + 65));
						toY++;
						break;
					case 'd':
						finalTarget = getTarget(getObjectAt(toX + 65 % 64 == 0 ? toX + 66 : toX + 65, toY + 1), getObjectAt(toX + 65 % 64 == 0 ? toX + 66 : toX + 65, toY + 63));
						toX++;
						break;
				}
				if (toX > -63 && toX < 1023 && toY > -63 && toY < 767)
				{
					if (!finalTarget.getType().isWall())
					{
						if (finalTarget.getType().isPlayer())
						{
							if (_slow)
							{
								entry.getKey().setType(PacmanObject.EMPTY);
								break;
							}
							else
							{
								for (final ScheduledFuture<?> future : _schedules)
									future.cancel(false);
								
								MapBuilder.getInstance().addScore(_totalStars - getStars().size());
								JOptionPane.showMessageDialog(null, "You lost!", "Noob", JOptionPane.INFORMATION_MESSAGE);
								setVisible(false);
								MapBuilder.getInstance().reset();
							}
						}
						
						entry.getKey().setLocation(toX, toY);
					}
					else
						entry.setValue('0');
				}
				else
				{
					if (toX == -64)
						toX = 1023;
					else if (toX == 1024)
						toX = -63;
					else if (toY == -64)
						toY = 767;
					else if (toY == 768)
						toY = -63;
					
					entry.getKey().setLocation(toX, toY);
				}
			}
		}
	}
}