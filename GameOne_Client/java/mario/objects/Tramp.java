package mario.objects;

import mario.SuperMario;
import mario.resources.JumpType;
import mario.MarioTaskManager;
import objects.mario.MarioType;
import util.Direction;

/**
 * Tramp object player uses to jump on.
 * @author Sahar
 */
public final class Tramp extends AbstractObject
{
	private static final long serialVersionUID = 2382929435743554621L;
	
	private static final int ON_JUMP_OFFSET = 10;
	
	private int _delay;
	
	public Tramp(final int x, final int y)
	{
		super(x, y, MarioType.TRAMP);
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		_delay = 0;
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_delay-- > 0)
			return;
		
		MarioTaskManager.getInstance().remove(this);
		setLocation(getX(), getY() - ON_JUMP_OFFSET);
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (dir != null)
		{
			_delay = 10;
			
			setLocation(getX(), getY() + ON_JUMP_OFFSET);
			
			MarioTaskManager.getInstance().add(this);
			SuperMario.getInstance().getPlayer().jump(JumpType.FLY);
		}
	}
}