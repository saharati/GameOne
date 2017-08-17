package mario.objects;

import objects.mario.MarioType;

public final class Cube extends AbstractObject
{
	private static final long serialVersionUID = -3023840813587997601L;
	
	public Cube(final int x, final int y)
	{
		super(x, y, MarioType.CUBE);
	}
}