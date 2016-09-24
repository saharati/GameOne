package mario.objects;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import mario.MarioScreen;
import mario.TaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;
import util.random.Rnd;

/**
 * A cube that falls from the sky, used after killing boss.
 * @author Sahar
 */
public final class FallingCube extends AbstractObject
{
	private static final long serialVersionUID = -8440410700066798066L;
	
	private static final int FALL_STARTPOINT = 15420;
	private static final int FALL_OFFSET = 420;
	
	public FallingCube()
	{
		super(FALL_STARTPOINT + Rnd.get(FALL_OFFSET), 0, MarioType.CUBE);
		
		TaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		setLocation(getX(), getY() + 1);
		
		final Map<Direction, List<AbstractObject>> objects = getNearbyObjects(new Rectangle(getX(), getY(), getWidth(), getHeight()));
		if (!objects.get(Direction.BELOW).isEmpty())
		{
			if (objects.get(Direction.BELOW).get(0) instanceof Player)
				MarioScreen.getInstance().getPlayer().deleteMe();
			else
				TaskManager.getInstance().remove(this);
		}
	}
}