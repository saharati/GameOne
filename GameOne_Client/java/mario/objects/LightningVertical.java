package mario.objects;

import mario.MarioScreen;
import mario.TaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * Vertical lightning object, delevels player if hit.
 * @author Sahar
 */
public final class LightningVertical extends AbstractObject
{
	private static final long serialVersionUID = 5341500261621411180L;
	
	private int _delay = 500;
	
	public LightningVertical(final int x, final int y)
	{
		super(x, y, MarioType.LIGHTNING2);
		
		TaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		_delay--;
		if (_delay == 0)
		{
			setVisible(!isVisible());
			_delay = 500;
		}
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (dir != null && isVisible())
			MarioScreen.getInstance().getPlayer().levelDown();
	}
	
	@Override
	protected boolean canGoThrough()
	{
		return !isVisible();
	}
}