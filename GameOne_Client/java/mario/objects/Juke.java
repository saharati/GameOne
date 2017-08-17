package mario.objects;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import mario.SuperMario;
import mario.resources.JumpType;
import mario.MarioTaskManager;
import objects.mario.MarioType;
import util.Direction;

public final class Juke extends AbstractObject
{
	private static final long serialVersionUID = 4648934166196245594L;
	
	private int _count;
	private boolean _direction;
	private boolean _isDead;
	
	public Juke(final int x, final int y)
	{
		super(x, y, MarioType.JUKE, MarioType.JUKE2, MarioType.JUKE3);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		do
		{
			setLocation(getX(), getY() + 1);
		} while (getNearbyObjects(new Rectangle(getX() - getWidth(), getY(), getWidth(), getHeight())).get(Direction.BELOW).isEmpty());
		
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
		if (_isDead)
		{
			_count++;
			
			if (_count % 10 == 0)
			{
				setVisible(!isVisible());
				
				if (_count == 150)
					deleteMe();
			}
		}
		else
		{
			_count++;
			
			if (_count == 80)
			{
				if (getCurrentImage() == getImages()[0])
					setCurrentImage(getImages()[1]);
				else
					setCurrentImage(getImages()[0]);
				
				_count = 0;
			}
			else if (_count % 2 == 0)
			{
				if (_direction)
				{
					final Map<Direction, List<AbstractObject>> objects = getNearbyObjects(new Rectangle(getX() - getWidth(), getY(), getWidth(), getHeight()));
					if (objects.get(Direction.BELOW).isEmpty())
						_direction = false;
					else
					{
						if (objects.get(Direction.LEFT).isEmpty())
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
				}
				else
				{
					final Map<Direction, List<AbstractObject>> objects = getNearbyObjects(new Rectangle(getX() + getWidth(), getY(), getWidth(), getHeight()));
					if (objects.get(Direction.BELOW).isEmpty())
						_direction = true;
					else
					{
						if (objects.get(Direction.RIGHT).isEmpty())
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
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (dir == null)
		{
			_isDead = true;
			setCurrentImage(getImages()[2]);
		}
		else if (dir == Direction.BELOW)
		{
			_isDead = true;
			setCurrentImage(getImages()[2]);
			SuperMario.getInstance().getPlayer().jump(JumpType.JUMP);
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