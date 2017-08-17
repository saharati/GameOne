package mario.objects;

import mario.SuperMario;
import objects.mario.MarioType;

public final class TubeExit extends AbstractObject
{
	private static final long serialVersionUID = 4459335759927808158L;
	
	public TubeExit(final int x, final int y)
	{
		super(x, y, MarioType.TUBE2);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		SuperMario.getInstance().getMapHolder().setComponentZOrder(this, 2);
	}
}