package mario.objects;

import mario.MarioScreen;
import objects.mario.MarioType;

/**
 * A ground object with grass on top and additional GroundAbove object attached to it.
 * @author Sahar
 */
public final class Ground extends AbstractObject
{
	private static final long serialVersionUID = -7243162070722696845L;
	
	public Ground(final int x, final int y)
	{
		super(x, y, MarioType.GROUND);
		
		MarioScreen.getInstance().add(new GroundAbove(x, y - 13));
	}
}