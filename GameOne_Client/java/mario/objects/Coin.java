package mario.objects;

import mario.SuperMario;
import mario.MarioTaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * A coin object that can be collected by Player.
 * @author Sahar
 */
public final class Coin extends AbstractObject
{
	private static final long serialVersionUID = -9142286700808876795L;
	
	private int _count;
	private int _regCount;
	
	public Coin(final int x, final int y)
	{
		super(x, y, MarioType.COIN, MarioType.COIN2);
	}
	
	public void animate()
	{
		_count = 50;
		
		MarioTaskManager.getInstance().add(this);
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
		if (_count > 0)
		{
			setLocation(getX(), getY() - 1);
			_count--;
			
			if (_count == 0)
				deleteMe();
			
			if (getIcon() == getTypes()[0].getIcon())
				setIcon(getTypes()[1].getIcon());
			else
				setIcon(getTypes()[0].getIcon());
		}
		else if (_regCount == 100)
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
			deleteMe();
	}
	
	@Override
	protected void deleteMe()
	{
		super.deleteMe();
		
		SuperMario.getInstance().increaseScore();
	}
	
	@Override
	protected boolean canGoThrough()
	{
		return true;
	}
}