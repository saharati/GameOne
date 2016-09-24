package mario.objects;

import objects.mario.MarioType;

/**
 * A cube object, does nothing special.
 * @author Sahar
 */
public final class Cube extends AbstractObject
{
	private static final long serialVersionUID = -3023840813587997601L;
	
	public Cube(final int x, final int y)
	{
		super(x, y, MarioType.CUBE);
	}
}