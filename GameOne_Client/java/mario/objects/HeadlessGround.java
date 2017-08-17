package mario.objects;

import objects.mario.MarioType;

public final class HeadlessGround extends AbstractObject
{
	private static final long serialVersionUID = 4544228140927670759L;
	
	public HeadlessGround(final int x, final int y)
	{
		super(x, y, MarioType.GROUND2);
	}
}