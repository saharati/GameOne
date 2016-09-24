package mario.objects;

import java.awt.Point;

import mario.MarioBuilder;
import mario.MarioScreen;
import mario.TaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * A floating block used to travel around.
 * @author Sahar
 */
public final class Flat extends AbstractObject
{
	private static final long serialVersionUID = 6180703091493796497L;
	
	private final Point _startPoint;
	private Flat _lastFlat;
	private Player _player;
	private boolean _count;
	private boolean _staticDirection;
	private boolean _direction;
	
	public Flat(final int x, final int y)
	{
		super(x, y, MarioType.FLAT);
		
		_startPoint = new Point(x, y);
	}
	
	public final void setReady(final boolean staticDirection, final Flat lastFlat)
	{
		_staticDirection = staticDirection;
		_lastFlat = lastFlat;
		_player = MarioScreen.getInstance().getPlayer();
		
		TaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		_count = !_count;
		if (_count)
			return;
		
		final boolean onBoard = getNearbyObjects(getBounds()).get(Direction.ABOVE).contains(_player);
		if (_staticDirection)
		{
			if (_direction)
			{
				setLocation(getX() - 1, getY());
				
				if (onBoard)
					_player.setLocation(_player.getX() - 1, _player.getY());
			}
			else
			{
				setLocation(getX() + 1, getY());
				
				if (onBoard)
					_player.setLocation(_player.getX() + 1, _player.getY());
			}
			
			if (getX() == _lastFlat.getX() || getX() == _startPoint.getX())
				_direction = !_direction;
		}
		else
		{
			if (_direction)
			{
				setLocation(getX(), getY() - 1);
				
				if (onBoard)
					_player.setLocation(_player.getX(), _player.getY() - 1);
			}
			else
			{
				setLocation(getX(), getY() + 1);
				
				if (onBoard)
					_player.setLocation(_player.getX(), _player.getY() + 1);
			}
			
			if (getY() == _lastFlat.getY() || getY() == _startPoint.getY())
				_direction = !_direction;
		}
		
		if (_player.getX() > MarioBuilder.SCREEN_MOVING_POINT)
			MarioScreen.getInstance().getBg().setLocation(-(_player.getX() - MarioBuilder.SCREEN_MOVING_POINT), MarioScreen.getInstance().getBg().getY());
	}
}