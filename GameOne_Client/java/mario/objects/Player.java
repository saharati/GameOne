package mario.objects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import mario.SuperMario;
import mario.resources.JumpType;
import mario.MarioTaskManager;
import objects.mario.MarioType;
import util.Direction;
import util.random.Rnd;

/**
 * The player instance.
 * @author Sahar
 */
public final class Player extends AbstractObject
{
	private static final long serialVersionUID = -5619383353466379122L;
	
	private static final MarioType[][] TYPES_PER_LEVEL = 
	{
		{MarioType.PLAYER, MarioType.PLAYER2},
		{MarioType.MARIO, MarioType.MARIO2},
		{MarioType.SUPERMARIO, MarioType.SUPERMARIO2}
	};
	private static final int LEVEL_OFFSET = 11;
	
	private int _level;
	private int _jumpCount;
	private int _invisCount;
	private int _swallowCount;
	private int _delay;
	private Direction _dir;
	private JumpType _jumpType = JumpType.NONE;
	
	public Player(final int x, final int y)
	{
		super(x, y, MarioType.PLAYER, MarioType.PLAYER2);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		MarioTaskManager.getInstance().add(this);
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		_level = 0;
		_jumpCount = 0;
		_invisCount = 0;
		_swallowCount = 0;
		_delay = 0;
		_dir = null;
		_jumpType = JumpType.NONE;
	}
	
	public void levelUp()
	{
		if (_level == 2)
			return;
		
		_level++;
		
		setImages(TYPES_PER_LEVEL[_level]);
		setBounds(getX(), (_level == 1 ? getY() - LEVEL_OFFSET : getY()), getCurrentImage().getWidth(null), getCurrentImage().getHeight(null));
	}
	
	public void levelDown()
	{
		if (_invisCount > 0 || _swallowCount > 0 || _level == -1)
			return;
		
		_invisCount = 300;
		_level--;
		
		if (_level == -1)
		{
			deleteMe();
			return;
		}
		
		setImages(TYPES_PER_LEVEL[_level]);
		setBounds(getX(), (_level == 1 ? getY() - LEVEL_OFFSET : getY()), getCurrentImage().getWidth(null), getCurrentImage().getHeight(null));
	}
	
	public void jump(final JumpType type)
	{
		_jumpCount = 0;
		_jumpType = type;
	}
	
	public void swallow()
	{
		_swallowCount = getHeight();
	}
	
	public boolean isJumping()
	{
		return _jumpType != JumpType.NONE;
	}
	
	public void setDirection(final Direction dir)
	{
		_dir = dir;
	}
	
	public Direction getDirection()
	{
		return _dir;
	}
	
	public boolean canShoot()
	{
		if (_level != 2)
			return false;
		if (_delay > 0)
			return false;
		
		return true;
	}
	
	public void shoot(final Direction dir)
	{
		_delay = 500;
		
		SuperMario.getInstance().addObject(new Shoot((dir == Direction.RIGHT ? (int) getBounds().getMaxX() + 1 : getX() - 1), (int) getBounds().getCenterY(), getY(), dir), true);
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_delay > 0)
			_delay--;
		if (_swallowCount != 0)
		{
			if (_swallowCount > 0)
			{
				setLocation(getX(), getY() + 1);
				_swallowCount--;
				
				if (_swallowCount == 0)
				{
					final TubeExit rnd = Rnd.get(SuperMario.getInstance().getExitTubes());
					
					setLocation(rnd.getX() + (rnd.getWidth() - getWidth()) / 2, rnd.getY() + rnd.getHeight() - getHeight());
					
					_swallowCount = -getHeight();
				}
			}
			else
			{
				setLocation(getX(), getY() + 1);
				_swallowCount++;
			}
			
			if (getX() > SuperMario.SCREEN_MOVING_POINT)
				SuperMario.getInstance().getMapHolder().setLocation(-(getX() - SuperMario.SCREEN_MOVING_POINT), SuperMario.getInstance().getMapHolder().getY());
			else
				SuperMario.getInstance().getMapHolder().setLocation(0, SuperMario.getInstance().getMapHolder().getY());
		}
		else
		{
			final Map<Direction, List<AbstractObject>> objects = getNearbyObjects(getBounds());
			if (_dir != null)
			{
				if (_dir == Direction.LEFT)
				{
					if (getX() > 0)
					{
						if (objects.get(Direction.LEFT).isEmpty())
							setLocation(getX() - 1, getY());
						else
						{
							boolean canGoThroughAll = true;
							for (AbstractObject o : objects.get(Direction.LEFT))
								if (!o.canGoThrough())
									canGoThroughAll = false;
							
							if (canGoThroughAll)
								setLocation(getX() - 1, getY());
						}
					}
					
					if (getCurrentImage() == TYPES_PER_LEVEL[_level][0].getFlippedIcon().getImage())
						setCurrentImage(TYPES_PER_LEVEL[_level][1].getFlippedIcon().getImage());
					else
						setCurrentImage(TYPES_PER_LEVEL[_level][0].getFlippedIcon().getImage());
				}
				else
				{
					if (objects.get(Direction.RIGHT).isEmpty())
						setLocation(getX() + 1, getY());
					else
					{
						boolean canGoThroughAll = true;
						for (AbstractObject o : objects.get(Direction.RIGHT))
							if (!o.canGoThrough())
								canGoThroughAll = false;
						
						if (canGoThroughAll)
							setLocation(getX() + 1, getY());
					}
					
					if (getCurrentImage() == getImages()[0])
						setCurrentImage(getImages()[1]);
					else
						setCurrentImage(getImages()[0]);
				}
				
				if (getX() > SuperMario.SCREEN_MOVING_POINT)
					SuperMario.getInstance().getMapHolder().setLocation(-(getX() - SuperMario.SCREEN_MOVING_POINT), SuperMario.getInstance().getMapHolder().getY());
			}
			
			if (_invisCount > 0)
			{
				if (_invisCount % 2 == 0)
					setVisible(!isVisible());
				
				_invisCount--;
				if (_invisCount == 0 && !isVisible())
					setVisible(true);
			}
			
			if (_jumpType != JumpType.NONE)
			{
				_jumpCount++;
				if (_jumpType == JumpType.JUMP && _jumpCount < 100 || _jumpType == JumpType.FLY && _jumpCount < 250)
				{
					if (objects.get(Direction.ABOVE).isEmpty())
						setLocation(getX(), getY() - 1);
					else
					{
						boolean canGoThroughAll = true;
						for (AbstractObject o : objects.get(Direction.ABOVE))
							if (!o.canGoThrough())
								canGoThroughAll = false;
						
						if (canGoThroughAll)
							setLocation(getX(), getY() - 1);
						else
							_jumpCount = 250;
					}
				}
				else
				{
					_jumpType = JumpType.FALL;
					
					if (objects.get(Direction.BELOW).isEmpty())
						setLocation(getX(), getY() + 1);
					else
					{
						_jumpCount = 0;
						_jumpType = JumpType.NONE;
					}
				}
			}
			else if (objects.get(Direction.BELOW).isEmpty())
				setLocation(getX(), getY() + 1);
			else
			{
				boolean canGoThroughAll = true;
				for (AbstractObject o : objects.get(Direction.BELOW))
					if (!o.canGoThrough())
						canGoThroughAll = false;
				
				if (canGoThroughAll)
					setLocation(getX(), getY() + 1);
			}
			
			if (getY() > SuperMario.SCREEN_SIZE.height)
				deleteMe();
			else
				for (final Entry<Direction, List<AbstractObject>> os : objects.entrySet())
					for (final AbstractObject o : os.getValue())
						o.onMeetObject(os.getKey());
		}
	}
	
	@Override
	protected void deleteMe()
	{
		super.deleteMe();
		
		MarioTaskManager.getInstance().stop();
		JOptionPane.showMessageDialog(null, "Try better next time.", "You lost!", JOptionPane.INFORMATION_MESSAGE);
		SuperMario.getInstance().onEnd();
	}
}