package pacman.objects;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.WindowConstants;

import client.Client;
import network.request.RequestUpdateGameScore;
import objects.GameResult;
import objects.pacman.PacmanObject;
import pacman.PacmanBuilder;
import pacman.objects.PacmanMapObject;
import util.random.Rnd;
import util.threadpool.ThreadPool;

/**
 * A map level in pacman.
 * @author Sahar
 */
public final class PacmanMap extends JFrame
{
	private static final long serialVersionUID = -9081629023961869616L;
	
	protected static final List<Character> DIRECTIONS = new ArrayList<>();
	static
	{
		DIRECTIONS.add('w');
		DIRECTIONS.add('a');
		DIRECTIONS.add('s');
		DIRECTIONS.add('d');
	}
	
	protected final ScheduledFuture<?>[] _schedules = new ScheduledFuture<?>[3];
	protected final Map<PacmanMapObject, Character> _mobs = new ConcurrentHashMap<>();
	protected final char[] _nextMoves = new char[2];
	protected final int _totalStars;
	protected PacmanMapObject _player;
	protected boolean _slow;
	protected int _slowTime;
	
	private final PacmanMapObject[][] _objects;
	
	public PacmanMap(final PacmanMapObject[][] objects)
	{
		super("GameOne Client - Pacman");
		
		_objects = objects;
		_totalStars = getStars().size();
		
		setLayout(new GridBagLayout());
		
		final GridBagConstraints gc = new GridBagConstraints();
		for (int i = 0;i < _objects.length;i++)
		{
			for (int j = 0;j < _objects[i].length;j++)
			{
				gc.gridx = i + 1;
				gc.gridy = j + 1;
				
				if (_objects[i][j].getType().isPlayer())
					_player = _objects[i][j];
				else if (_objects[i][j].getType().isMonster())
					_mobs.put(_objects[i][j], '0');
				
				add(_objects[i][j], gc);
			}
		}
		
		_mobs.keySet().forEach(mob -> getContentPane().setComponentZOrder(mob, 1));
		getContentPane().setComponentZOrder(_player, 0);
		getContentPane().setBackground(Color.BLACK);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		addKeyListener(new Movement());
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		for (final ScheduledFuture<?> future : _schedules)
			if (future != null)
				future.cancel(false);
		
		PacmanBuilder.getInstance().addScore(_totalStars - getStars().size());
		
		final boolean isWin = PacmanBuilder.getInstance().getCurrentMap() == null;
		final int totalScore = PacmanBuilder.getInstance().getCurrentScore();
		
		Client.getInstance().sendPacket(new RequestUpdateGameScore(isWin ? GameResult.WIN : GameResult.LOSE, totalScore));
		
		PacmanBuilder.getInstance().reset();
		PacmanBuilder.getInstance().setVisible(true);
	}
	
	public PacmanMapObject[][] getObjects()
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
	
	protected List<PacmanMapObject> getStars()
	{
		final List<PacmanMapObject> stars = new ArrayList<>();
		for (int i = 0;i < _objects.length;i++)
			for (int j = 0;j < _objects[i].length;j++)
				if (_objects[i][j].getType().isStar())
					stars.add(_objects[i][j]);
		
		return stars;
	}
	
	protected PacmanMapObject getObjectAt(final int x, final int y)
	{
		for (int i = 0;i < _objects.length;i++)
			for (int j = 0;j < _objects[i].length;j++)
				if (!_objects[i][j].getType().isEmpty() && isWithin(x, y, _objects[i][j].getX(), _objects[i][j].getY()))
					return _objects[i][j];
		
		return PacmanMapObject.EMPTY;
	}
	
	protected void checkForDirectionChange(final int x, final int y)
	{
		// If we don't have any next moves planned, ignore.
		if (_nextMoves[1] == 0)
			return;
		// If we are trying to go opposite direction, change instantly.
		final char oppositeDirection = getOppositeDirection();
		if (oppositeDirection == _nextMoves[0])
		{
			_nextMoves[0] = _nextMoves[1];
			_nextMoves[1] = 0;
			return;
		}
		// If we are not in a place that allows turning, ignore.
		if (x % PacmanBuilder.BLOCK_SIZE != 0 || y % PacmanBuilder.BLOCK_SIZE != 0)
			return;
		// If we are out of bounds, don't change direction even if possible.
		if (x < 0 || y < 0 || x + PacmanBuilder.BLOCK_SIZE > getContentPane().getWidth() || y + PacmanBuilder.BLOCK_SIZE > getContentPane().getHeight())
			return;
		
		// Finally try to change direction...
		// Cover corner cases.
		switch (_nextMoves[1])
		{
			case 'w':
				final int upY = y - 1 < 0 ? getContentPane().getHeight() - 1 : y - 1;
				if (!getObjectAt(x + PacmanBuilder.BLOCK_SIZE / 2, upY).getType().isWall())
				{
					_nextMoves[1] = 0;
					_nextMoves[0] = 'w';
				}
				break;
			case 'a':
				final int leftX = x - 1 < 0 ? getContentPane().getWidth() - 1 : x - 1;
				if (!getObjectAt(leftX, y + PacmanBuilder.BLOCK_SIZE / 2).getType().isWall())
				{
					_nextMoves[1] = 0;
					_nextMoves[0] = 'a';
				}
				break;
			case 's':
				final int downY = y + PacmanBuilder.BLOCK_SIZE + 1 > getContentPane().getHeight() ? 1 : y + PacmanBuilder.BLOCK_SIZE + 1;
				if (!getObjectAt(x + PacmanBuilder.BLOCK_SIZE / 2, downY).getType().isWall())
				{
					_nextMoves[1] = 0;
					_nextMoves[0] = 's';
				}
				break;
			case 'd':
				final int rightX = x + PacmanBuilder.BLOCK_SIZE + 1 > getContentPane().getWidth() ? 1 : x + PacmanBuilder.BLOCK_SIZE + 1;
				if (!getObjectAt(rightX, y + PacmanBuilder.BLOCK_SIZE / 2).getType().isWall())
				{
					_nextMoves[1] = 0;
					_nextMoves[0] = 'd';
				}
				break;
		}
	}
	
	protected char getRandomDirectionChange(final int x, final int y, final char direction)
	{
		// If we are not in a place that allows turning, ignore.
		if (x % PacmanBuilder.BLOCK_SIZE != 0 || y % PacmanBuilder.BLOCK_SIZE != 0)
			return direction;
		// We shouldn't always change direction when available.
		if (Rnd.nextBoolean())
			return direction;
		// If we are out of bounds, don't change direction even if possible.
		if (x < 0 || y < 0 || x + PacmanBuilder.BLOCK_SIZE > getContentPane().getWidth() || y + PacmanBuilder.BLOCK_SIZE > getContentPane().getHeight())
			return direction;
		
		// Finally try to change direction...
		// Cover corner cases.
		switch (direction)
		{
			// If currently going up or down.
			case 'w':
			case 's':
				// Check if can change direction to left or right.
				final int leftX = x - 1 < 0 ? getContentPane().getWidth() - 1 : x - 1;
				final int rightX = x + PacmanBuilder.BLOCK_SIZE + 1 > getContentPane().getWidth() ? 1 : x + PacmanBuilder.BLOCK_SIZE + 1;
				final boolean canGoLeft = !getObjectAt(leftX, y + PacmanBuilder.BLOCK_SIZE / 2).getType().isWall();
				final boolean canGoRight = !getObjectAt(rightX, y + PacmanBuilder.BLOCK_SIZE / 2).getType().isWall();
				// If we can only go to one direction, just pick that direction.
				if (canGoLeft != canGoRight)
				{
					if (canGoLeft)
						return 'a';
					
					return 'd';
				}
				// Else if we can go both directions, pick randomly.
				if (canGoLeft && canGoRight)
				{
					if (Rnd.nextBoolean())
						return 'a';
					
					return 'd';
				}
				break;
			// If currently going left or right.
			case 'a':
			case 'd':
				// Check if can change direction to up or down.
				final int upY = y - 1 < 0 ? getContentPane().getHeight() - 1 : y - 1;
				final int downY = y + PacmanBuilder.BLOCK_SIZE + 1 > getContentPane().getHeight() ? 1 : y + PacmanBuilder.BLOCK_SIZE + 1;
				final boolean canGoUp = !getObjectAt(x + PacmanBuilder.BLOCK_SIZE / 2, upY).getType().isWall();
				final boolean canGoDown = !getObjectAt(x + PacmanBuilder.BLOCK_SIZE / 2, downY).getType().isWall();
				// If we can only go to one direction, just pick that direction.
				if (canGoUp != canGoDown)
				{
					if (canGoUp)
						return 'w';
					
					return 's';
				}
				// Else if we can go both directions, pick randomly.
				if (canGoUp && canGoDown)
				{
					if (Rnd.nextBoolean())
						return 'w';
					
					return 's';
				}
				break;
		}
		
		// No new direction found.
		return direction;
	}
	
	protected void setSlow()
	{
		_slowTime = 1500;
		_slow = true;
	}
	
	private static boolean isWithin(final int x, final int y, final int objX, final int objY)
	{
		return x > objX && x < objX + PacmanBuilder.BLOCK_SIZE && y > objY && y < objY + PacmanBuilder.BLOCK_SIZE;
	}
	
	private char getOppositeDirection()
	{
		switch (_nextMoves[1])
		{
			case 'w':
				return 's';
			case 'a':
				return 'd';
			case 's':
				return 'w';
			case 'd':
				return 'a';
		}
		
		return 0;
	}
	
	protected class Movement extends KeyAdapter
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
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					ch = 's';
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					ch = 'd';
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

	protected class Animation implements Runnable
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

	protected class PlayerMove implements Runnable
	{
		@Override
		public void run()
		{
			int toX = _player.getX();
			int toY = _player.getY();
			checkForDirectionChange(toX, toY);
			
			PacmanMapObject finalTarget = null;
			switch (_nextMoves[0])
			{
				case 'w':
					final int upY = toY - 1 < 0 ? getContentPane().getHeight() - 1 : toY - 1;
					finalTarget = getObjectAt(toX + PacmanBuilder.BLOCK_SIZE / 2, upY);
					toY--;
					break;
				case 'a':
					final int leftX = toX - 1 < 0 ? getContentPane().getWidth() - 1 : toX - 1;
					finalTarget = getObjectAt(leftX, toY + PacmanBuilder.BLOCK_SIZE / 2);
					toX--;
					break;
				case 's':
					final int downY = toY + PacmanBuilder.BLOCK_SIZE + 1 > getContentPane().getHeight() ? 1 : toY + PacmanBuilder.BLOCK_SIZE + 1;
					finalTarget = getObjectAt(toX + PacmanBuilder.BLOCK_SIZE / 2, downY);
					toY++;
					break;
				default:
					final int rightX = toX + PacmanBuilder.BLOCK_SIZE + 1 > getContentPane().getWidth() ? 1 : toX + PacmanBuilder.BLOCK_SIZE + 1;
					finalTarget = getObjectAt(rightX, toY + PacmanBuilder.BLOCK_SIZE / 2);
					toX++;
					break;
			}
			
			// If inside map bounds.
			if (toX > -PacmanBuilder.BLOCK_SIZE && toX < getContentPane().getWidth() && toY > -PacmanBuilder.BLOCK_SIZE && toY < getContentPane().getHeight())
			{
				// If the target isn't a wall.
				if (!finalTarget.getType().isWall())
				{
					// If its a star.
					if (finalTarget.getType().isStar())
					{
						// Consume it.
						finalTarget.setType(PacmanObject.EMPTY);
						
						// If no more stars on map, finish this map.
						if (getStars().isEmpty())
						{
							for (final ScheduledFuture<?> future : _schedules)
								future.cancel(false);
							
							JOptionPane.showMessageDialog(null, "You won!", "Pazam!", JOptionPane.INFORMATION_MESSAGE);
							
							final PacmanMap next = PacmanBuilder.getInstance().getNextMap();
							if (next != null)
							{
								PacmanBuilder.getInstance().addScore(_totalStars);
								
								setVisible(false);
								next.setVisible(true);
							}
							else
								dispose();
						}
					}
					// If its food.
					else if (finalTarget.getType().isFood())
					{
						// Consume it.
						finalTarget.setType(PacmanObject.EMPTY);
						
						// Activate slow on mobs.
						setSlow();
					}
					// If its a mob.
					else if (finalTarget.getType().isMonster())
					{
						// If its in slow mode, consume it.
						if (_slow)
							finalTarget.setType(PacmanObject.EMPTY);
						// Otherwise player lose, end map.
						else
						{
							dispose();
							
							JOptionPane.showMessageDialog(null, "You lost!", "Noob", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					
					// Finally if all ok, set location to new location.
					_player.setLocation(toX, toY);
					if (_player.getType() != PacmanObject.PLAYER_NORMAL)
						_player.setType(PacmanObject.getObjectForDirection(_nextMoves[0]));
				}
			}
			// If player out of map bounds.
			else
			{
				// Move the player to the opposite side of the screen.
				if (toX == -PacmanBuilder.BLOCK_SIZE)
					toX = getContentPane().getWidth();
				else if (toX == getContentPane().getWidth())
					toX = -PacmanBuilder.BLOCK_SIZE;
				else if (toY == -PacmanBuilder.BLOCK_SIZE)
					toY = getContentPane().getHeight();
				else if (toY == getContentPane().getHeight())
					toY = -PacmanBuilder.BLOCK_SIZE;
				
				// Update location.
				_player.setLocation(toX, toY);
				if (_player.getType() != PacmanObject.PLAYER_NORMAL)
					_player.setType(PacmanObject.getObjectForDirection(_nextMoves[0]));
			}
		}
	}
	
	protected class MobMove implements Runnable
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
					for (final Entry<PacmanMapObject, Character> entry : _mobs.entrySet())
					{
						if (entry.getKey().getType().isEmpty())
							continue;
						
						if (entry.getKey().getType().isSlow())
							entry.getKey().setType(entry.getKey().getReservedType());
						else
							entry.getKey().setType(PacmanObject.MOB_SLOW);
					}
				}
				
				_slow = --_slowTime != 0;
			}
			
			for (final Entry<PacmanMapObject, Character> entry : _mobs.entrySet())
			{
				if (entry.getKey().getType().isEmpty())
					continue;
				
				if (_slowTime > 500)
				{
					if (_slow)
					{
						if (entry.getKey().getType() != PacmanObject.MOB_SLOW)
							entry.getKey().setType(PacmanObject.MOB_SLOW);
					}
					else if (entry.getKey().getType().isSlow())
						entry.getKey().setType(entry.getKey().getReservedType());
				}
				else if (_slowTime == 0 && entry.getKey().getType().isSlow())
					entry.getKey().setType(entry.getKey().getReservedType());
				
				int toX = entry.getKey().getX();
				int toY = entry.getKey().getY();
				entry.setValue(getRandomDirectionChange(toX, toY, entry.getValue()));
				if (entry.getValue() == '0')
					entry.setValue(Rnd.get(DIRECTIONS));
				
				PacmanMapObject finalTarget = null;
				switch (entry.getValue())
				{
					case 'w':
						final int upY = toY - 1 < 0 ? getContentPane().getHeight() - 1 : toY - 1;
						finalTarget = getObjectAt(toX + PacmanBuilder.BLOCK_SIZE / 2, upY);
						toY--;
						break;
					case 'a':
						final int leftX = toX - 1 < 0 ? getContentPane().getWidth() - 1 : toX - 1;
						finalTarget = getObjectAt(leftX, toY + PacmanBuilder.BLOCK_SIZE / 2);
						toX--;
						break;
					case 's':
						final int downY = toY + PacmanBuilder.BLOCK_SIZE + 1 > getContentPane().getHeight() ? 1 : toY + PacmanBuilder.BLOCK_SIZE + 1;
						finalTarget = getObjectAt(toX + PacmanBuilder.BLOCK_SIZE / 2, downY);
						toY++;
						break;
					default:
						final int rightX = toX + PacmanBuilder.BLOCK_SIZE + 1 > getContentPane().getWidth() ? 1 : toX + PacmanBuilder.BLOCK_SIZE + 1;
						finalTarget = getObjectAt(rightX, toY + PacmanBuilder.BLOCK_SIZE / 2);
						toX++;
						break;
				}
				
				if (toX > -PacmanBuilder.BLOCK_SIZE && toX < getContentPane().getWidth() && toY > -PacmanBuilder.BLOCK_SIZE && toY < getContentPane().getHeight())
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
							
							dispose();
							
							JOptionPane.showMessageDialog(null, "You lost!", "Noob", JOptionPane.INFORMATION_MESSAGE);
						}
						
						entry.getKey().setLocation(toX, toY);
					}
					else
						entry.setValue('0');
				}
				else
				{
					if (toX == -PacmanBuilder.BLOCK_SIZE)
						toX = getContentPane().getWidth();
					else if (toX == getContentPane().getWidth())
						toX = -PacmanBuilder.BLOCK_SIZE;
					else if (toY == -PacmanBuilder.BLOCK_SIZE)
						toY = getContentPane().getHeight();
					else if (toY == getContentPane().getHeight())
						toY = -PacmanBuilder.BLOCK_SIZE;
					
					entry.getKey().setLocation(toX, toY);
				}
			}
		}
	}
}