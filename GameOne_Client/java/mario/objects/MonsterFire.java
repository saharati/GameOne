package mario.objects;

import java.util.List;
import java.util.Map;

import mario.MarioTaskManager;
import objects.mario.MarioType;
import util.Direction;
import util.random.Rnd;

/**
 * A shot made by a monster.
 * @author Sahar
 */
public final class MonsterFire extends AbstractObject
{
	private static final long serialVersionUID = 4667015215144191407L;
	
	private Direction _horizontalDirection;
	private Direction _verticalDirection;
	private int _timer;
	
	public MonsterFire(final int x, final int y)
	{
		super(x, y, MarioType.SHOOT);
		
		do
		{
			if (Rnd.nextBoolean())
				_horizontalDirection = Rnd.nextBoolean() ? Direction.LEFT : Direction.RIGHT;
			if (Rnd.nextBoolean())
				_verticalDirection = Rnd.nextBoolean() ? Direction.ABOVE : null;
		} while (_horizontalDirection == null);
		
		MarioTaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_timer++ == 1000)
		{
			deleteMe();
			return;
		}
		
		if (_horizontalDirection == Direction.RIGHT)
			setLocation(getX() + 1, getY() + (_verticalDirection != null ? -1 : 0));
		else if (_horizontalDirection == Direction.LEFT)
			setLocation(getX() - 1, getY() + (_verticalDirection != null ? -1 : 0));
		else
			setLocation(getX(), getY() - 1);
		
		final Map<Direction, List<AbstractObject>> obj = getNearbyObjects(getBounds());
		for (final List<AbstractObject> o : obj.values())
		{
			for (final AbstractObject a : o)
			{
				if (!(a instanceof MonsterFire) && !(a instanceof Alien2))
				{
					deleteMe();
					if (a instanceof Player)
						((Player) a).levelDown();
					
					break;
				}
			}
		}
	}
}