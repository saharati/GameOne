package mario.objects;

import mario.SuperMario;
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
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		SuperMario.getInstance().addObject(new GroundAbove(getX(), getY() - 13), true);
	}
}