package mario.objects;

import mario.SuperMario;
import mario.MarioTaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * Horizontal lightning object, delevels player if hit.
 * @author Sahar
 */
public final class LightningHorizontal extends AbstractObject
{
	private static final long serialVersionUID = 5341500261621411180L;
	
	private int _delay = 500;
	
	public LightningHorizontal(final int x, final int y)
	{
		super(x, y, MarioType.LIGHTNING);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		MarioTaskManager.getInstance().add(this);
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
		if (dir != null)
			SuperMario.getInstance().getPlayer().levelDown();
	}
}