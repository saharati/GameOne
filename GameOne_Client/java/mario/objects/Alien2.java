package mario.objects;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import mario.SuperMario;
import mario.MarioTaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * An alien boss object.
 * @author Sahar
 */
public final class Alien2 extends AbstractObject
{
	private static final long serialVersionUID = 75038392805825335L;
	
	private static final Point TELEPORT_LOCATION = new Point(15500, 600);
	
	private int _count;
	private boolean _direction;
	private boolean _isDead;
	
	public Alien2(final int x, final int y)
	{
		super(x, y + 1, MarioType.ALIENNEW);
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
		
		_count = 0;
		_isDead = false;
	}
	
	@Override
	public void notifyTimeOut()
	{
		_count++;
		
		if (_isDead)
		{
			if (_count % 10 == 0)
			{
				setVisible(!isVisible());
				
				if (_count == 150)
					deleteMe();
			}
		}
		else
		{
			if (_count % 2 == 0)
			{
				if (_direction)
				{
					final Map<Direction, List<AbstractObject>> objects = getNearbyObjects(new Rectangle(getX() - getWidth(), getY(), getWidth(), getHeight()));
					if (objects.get(Direction.BELOW).isEmpty())
						_direction = false;
					else if (objects.get(Direction.LEFT).isEmpty())
						setLocation(getX() - 1, getY());
					else
					{
						boolean canGoThroughAll = true;
						for (AbstractObject o : getNearbyObjects(getBounds()).get(Direction.LEFT))
							if (!o.canGoThrough())
								canGoThroughAll = false;
						
						if (canGoThroughAll)
							setLocation(getX() - 1, getY());
						else
							_direction = false;
					}
				}
				else
				{
					final Map<Direction, List<AbstractObject>> objects = getNearbyObjects(new Rectangle(getX() + getWidth(), getY(), getWidth(), getHeight()));
					if (objects.get(Direction.BELOW).isEmpty())
						_direction = true;
					else if (objects.get(Direction.RIGHT).isEmpty())
						setLocation(getX() + 1, getY());
					else
					{
						boolean canGoThroughAll = true;
						for (AbstractObject o : getNearbyObjects(getBounds()).get(Direction.RIGHT))
							if (!o.canGoThrough())
								canGoThroughAll = false;
						
						if (canGoThroughAll)
							setLocation(getX() + 1, getY());
						else
							_direction = true;
					}
				}
			}
			if (_count % 300 == 0)
			{
				SuperMario.getInstance().addObject(new MonsterFire((int) getBounds().getCenterX(), getY()), true);
				SuperMario.getInstance().addObject(new MonsterFire((int) getBounds().getCenterX(), getY()), true);
				SuperMario.getInstance().addObject(new MonsterFire((int) getBounds().getCenterX(), getY()), true);
			}
		}
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (dir == null)
		{
			_isDead = true;
			_count = 0;
			
			SuperMario.getInstance().changeBackground(MarioType.BACKGROUND2.getIcon());
			SuperMario.getInstance().getPlayer().setLocation(TELEPORT_LOCATION);
		}
		else
			SuperMario.getInstance().getPlayer().levelDown();
	}
	
	@Override
	protected boolean skip()
	{
		return _isDead;
	}
}