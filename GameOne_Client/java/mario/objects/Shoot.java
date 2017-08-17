package mario.objects;

import java.util.List;
import java.util.Map;

import mario.MarioTaskManager;
import objects.mario.MarioType;
import util.Direction;

public final class Shoot extends AbstractObject
{
	private static final long serialVersionUID = -9033654918966651435L;
	
	private final Direction _horizontalDirection;
	private Direction _verticalDirection = Direction.BELOW;
	private boolean _count;
	private int _topY;
	private int _timer;
	
	public Shoot(final int x, final int y, final int topY, final Direction horizontalDirection)
	{
		super(x, y, MarioType.SHOOT);
		
		_topY = topY;
		_horizontalDirection = horizontalDirection;
		
		MarioTaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_timer++ == 300)
		{
			deleteMe();
			return;
		}
		
		final Map<Direction, List<AbstractObject>> obj = getNearbyObjects(getBounds());
		if (_horizontalDirection == Direction.RIGHT)
		{
			setLocation(getX() + 1, (_count ? getY() + (_verticalDirection == Direction.BELOW ? 1 : -1) : getY()));
			if (!obj.get(Direction.RIGHT).isEmpty())
			{
				deleteMe();
				obj.get(Direction.RIGHT).get(0).onMeetObject(null);
			}
		}
		else
		{
			setLocation(getX() - 1, (_count ? getY() + (_verticalDirection == Direction.BELOW ? 1 : -1) : getY()));
			if (!obj.get(Direction.LEFT).isEmpty())
			{
				deleteMe();
				obj.get(Direction.LEFT).get(0).onMeetObject(null);
			}
		}
		
		_count = !_count;
		if (_verticalDirection == Direction.BELOW)
		{
			if (!obj.get(Direction.BELOW).isEmpty())
				_verticalDirection = Direction.ABOVE;
		}
		else if (getY() == _topY || !obj.get(Direction.ABOVE).isEmpty())
			_verticalDirection = Direction.BELOW;
	}
}