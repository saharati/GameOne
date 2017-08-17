package mario.objects;

import java.util.List;
import java.util.Map;

import mario.SuperMario;
import objects.mario.MarioType;
import util.Direction;

public final class Mushrum extends AbstractObject
{
	private static final long serialVersionUID = 5371512736462304206L;
	
	private int _y;
	private boolean _direction;
	
	public Mushrum(final int x, final int y)
	{
		super(x, y, MarioType.MUSHRUM);
		
		_y = getWidth();
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_y > 0)
		{
			setLocation(getX(), getY() - 1);
			_y--;
		}
		else
		{
			final Map<Direction, List<AbstractObject>> objects = getNearbyObjects(getBounds());
			if (objects.get(Direction.BELOW).isEmpty())
				setLocation(getX(), getY() + 1);
			else if (objects.get(Direction.RIGHT).isEmpty() && !_direction)
				setLocation(getX() + 1, getY());
			else if (objects.get(Direction.LEFT).isEmpty())
			{
				if (!_direction)
					_direction = true;
				
				setLocation(getX() - 1, getY());
			}
			else
				_direction = false;
			
			if (getX() < -getWidth() || getY() > SuperMario.SCREEN_SIZE.height)
				deleteMe();
		}
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (dir != null)
		{
			deleteMe();
			SuperMario.getInstance().getPlayer().levelUp();
		}
	}
	
	@Override
	protected boolean canGoThrough()
	{
		return true;
	}
}