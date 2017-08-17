package mario.objects;

import mario.SuperMario;
import mario.MarioTaskManager;
import objects.mario.MarioType;
import util.Direction;

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
			if (getCurrentImage() == getImages()[0])
				setCurrentImage(getImages()[1]);
			else
				setCurrentImage(getImages()[0]);
			
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