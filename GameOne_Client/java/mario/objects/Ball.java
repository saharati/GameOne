package mario.objects;

import java.util.List;
import java.util.Map;

import mario.MarioScreen;
import mario.TaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * A ball object shoot by Cannon.
 * @author Sahar
 */
public final class Ball extends AbstractObject
{
	private static final long serialVersionUID = -271362941270521530L;
	
	private int _timer;
	private int _velocity;
	
	public Ball(final int x, final int y)
	{
		super(x, y, MarioType.BALL);
		
		TaskManager.getInstance().add(this);
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
		
		_velocity++;
		if (_velocity < 60)
			setLocation(getX() - 1, getY() - (_velocity % 2 == 0 ? 1 : 0));
		else
			setLocation(getX() - 1, getY() + (obj.get(Direction.BELOW).isEmpty() ? 1 : 0));
		
		if (!obj.get(Direction.LEFT).isEmpty())
		{
			deleteMe();
			
			if (obj.get(Direction.LEFT).get(0) instanceof Player)
				MarioScreen.getInstance().getPlayer().levelDown();
		}
	}
}