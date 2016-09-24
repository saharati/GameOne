package mario.objects;

import mario.MarioScreen;
import mario.TaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * Flames spited by Flares object.
 * @author Sahar
 */
public final class Flames extends AbstractObject
{
	private static final long serialVersionUID = 4105154943435385601L;
	
	private int _regCount;

	public Flames(final int x, final int y, final MarioType... types)
	{
		super(x, y, types);
		
		setVisible(false);
		
		if (types.length > 1)
			TaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_regCount == 50)
		{
			if (getIcon() == getImages()[0])
				setIcon(getImages()[1]);
			else
				setIcon(getImages()[0]);
			
			_regCount = 0;
		}
		else
			_regCount++;
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