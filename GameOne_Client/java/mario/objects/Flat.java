package mario.objects;

import java.awt.Point;
import java.util.List;

import mario.SuperMario;
import mario.MarioTaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * A floating block used to travel around.
 * @author Sahar
 */
public final class Flat extends AbstractObject
{
	private static final long serialVersionUID = 6180703091493796497L;
	
	private List<Flat> _connectedFlats;
	private Point _startPoint;
	private Flat _lastFlat;
	private boolean _isVertical;
	private boolean _count;
	private boolean _direction;
	
	public Flat(final int x, final int y)
	{
		super(x, y, MarioType.FLAT);
	}
	
	public void setReady(final boolean isVertical, final List<Flat> connectedFlats)
	{
		_connectedFlats =	connectedFlats;
		_startPoint = getLocation();
		_lastFlat = _connectedFlats.get(_connectedFlats.size() - 1);
		_isVertical = isVertical;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		if (_connectedFlats != null)
		{
			for (final Flat flat : _connectedFlats)
				if (flat != this)
					flat.setVisible(false);
		
			MarioTaskManager.getInstance().add(this);
		}
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		_connectedFlats = null;
		_startPoint = null;
		_lastFlat = null;
		_direction = false;
	}
	
	@Override
	public void notifyTimeOut()
	{
		_count = !_count;
		if (_count)
			return;
		
		final Player player = SuperMario.getInstance().getPlayer();
		final boolean onBoard = getNearbyObjects(getBounds()).get(Direction.ABOVE).contains(player);
		if (_isVertical)
		{
			if (_direction)
			{
				setLocation(getX(), getY() - 1);
				
				if (onBoard)
					player.setLocation(player.getX(), player.getY() - 1);
			}
			else
			{
				setLocation(getX(), getY() + 1);
				
				if (onBoard)
					player.setLocation(player.getX(), player.getY() + 1);
			}
			
			if (getY() == _lastFlat.getY() || getY() == _startPoint.getY())
				_direction = !_direction;
		}
		else
		{
			if (_direction)
			{
				setLocation(getX() - 1, getY());
				
				if (onBoard)
					player.setLocation(player.getX() - 1, player.getY());
			}
			else
			{
				setLocation(getX() + 1, getY());
				
				if (onBoard)
					player.setLocation(player.getX() + 1, player.getY());
			}
			
			if (getX() == _lastFlat.getX() || getX() == _startPoint.getX())
				_direction = !_direction;
		}
		
		if (player.getX() > SuperMario.SCREEN_MOVING_POINT)
			SuperMario.getInstance().getMapHolder().setLocation(-(player.getX() - SuperMario.SCREEN_MOVING_POINT), SuperMario.getInstance().getMapHolder().getY());
	}
}