package mario.objects;

import mario.SuperMario;
import mario.MarioTaskManager;
import objects.mario.MarioType;
import util.Direction;

public final class Flower extends AbstractObject
{
	private static final long serialVersionUID = -1352175284717660778L;
	
	private final int _initialY;
	private int _count;
	private int _delay;
	private boolean _direction;
	
	protected Flower(final int x, final int y)
	{
		super(x, y, MarioType.FLOWER, MarioType.FLOWER2);
		
		_initialY = y;
		
		MarioTaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		_count++;
		if (_count == 105)
		{
			if (getCurrentImage() == getImages()[0])
				setCurrentImage(getImages()[1]);
			else
				setCurrentImage(getImages()[0]);
			
			_count = 0;
		}
		if (_delay > 0)
		{
			_delay--;
			return;
		}
		if (_direction)
		{
			if (getY() == _initialY)
			{
				_direction = !_direction;
				_delay = 1000;
			}
		}
		else if (getY() == _initialY - 105)
		{
			_direction = !_direction;
			_delay = 1000;
		}
		if (_count % 10 == 0)
			setLocation(getX(), getY() + (_direction ? 1 : -1));
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (dir == null)
			deleteMe();
		else
			SuperMario.getInstance().getPlayer().levelDown();
	}
}