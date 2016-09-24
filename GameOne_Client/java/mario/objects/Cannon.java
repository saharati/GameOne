package mario.objects;

import mario.MarioScreen;
import mario.TaskManager;
import objects.mario.MarioType;

/**
 * A cannon that shoots instances of Ball.
 * @author Sahar
 */
public final class Cannon extends AbstractObject
{
	private static final long serialVersionUID = 6115647376073107854L;
	
	private int _count;
	
	public Cannon(final int x, final int y)
	{
		super(x, y, MarioType.CANNON);
		
		TaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		_count++;
		if (_count < 600)
			return;
		
		MarioScreen.getInstance().add(new Ball(getX(), getY()));
		
		_count = 0;
	}
}