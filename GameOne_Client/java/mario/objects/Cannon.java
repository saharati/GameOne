package mario.objects;

import mario.SuperMario;
import mario.MarioTaskManager;
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
		_count++;
		if (_count < 600)
			return;
		
		SuperMario.getInstance().addObject(new Ball(getX(), getY()), true);
		
		_count = 0;
	}
}