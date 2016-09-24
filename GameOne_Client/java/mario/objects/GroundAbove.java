package mario.objects;

import objects.mario.MarioType;

/**
 * A static object just to give a better look to ground's grass.
 * @author Sahar
 */
public final class GroundAbove extends AbstractObject
{
	private static final long serialVersionUID = 1132835380671630806L;
	
	public GroundAbove(final int x, final int y)
	{
		super(x, y, MarioType.GROUNDABOVE);
	}
	
	@Override
	protected boolean skip()
	{
		return true;
	}
}