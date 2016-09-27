package mario.objects;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import mario.SuperMario;
import mario.MarioTaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * An alien object.
 * @author Sahar
 */
public final class Alien extends AbstractObject
{
	private static final long serialVersionUID = 3074246994132545775L;
	
	private int _count;
	private boolean _direction;
	private boolean _isDead;
	
	public Alien(final int x, final int y)
	{
		super(x, y + 1, MarioType.ALIEN, MarioType.ALIEN2);
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
						for (final AbstractObject o : getNearbyObjects(getBounds()).get(Direction.LEFT))
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
		}
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (dir == null)
		{
			_isDead = true;
			_count = 0;
			
			setIcon(getTypes()[1].getIcon());
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