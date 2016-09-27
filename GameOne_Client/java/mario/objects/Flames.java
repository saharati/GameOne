package mario.objects;

import mario.SuperMario;
import mario.MarioTaskManager;
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
			MarioTaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_regCount == 50)
		{
			if (getIcon() == getTypes()[0].getIcon())
				setIcon(getTypes()[1].getIcon());
			else
				setIcon(getTypes()[0].getIcon());
			
			_regCount = 0;
		}
		else
			_regCount++;
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (dir != null)
			SuperMario.getInstance().getPlayer().levelDown();
	}
}