package mario.objects;

import mario.TaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * A wall object.
 * @author Sahar
 */
public final class Wall extends AbstractObject
{
	private static final long serialVersionUID = -4775183339343182254L;
	
	private boolean _direction;
	private int _count;
	private int _total;
	
	public Wall(final int x, final int y)
	{
		super(x, y, MarioType.WALL);
	}
	
	@Override
	public void notifyTimeOut()
	{
		_total++;
		
		if (_total == 50)
			deleteMe();
		else
		{
			_count += _direction ? -1 : 1;
			if (_count == 2 || _count == -2)
				_direction = !_direction;
			
			setLocation(getX() + _count, getY());
		}
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (TaskManager.getInstance().contains(this))
			return;
		if (dir != Direction.ABOVE)
			return;
		
		TaskManager.getInstance().add(this);
	}
}