package mario.objects;

import mario.SuperMario;
import objects.mario.MarioType;

public final class TubeEntrance extends AbstractObject
{
	private static final long serialVersionUID = 8124283463462070586L;
	
	// (TubeWidth - FlowerWidth) / 2
	private static final int X_OFFSET = 7;
	// Static 5.
	private static final int Y_OFFSET = 5;
	
	public TubeEntrance(final int x, final int y)
	{
		super(x, y, MarioType.TUBE);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		SuperMario.getInstance().addObject(new Flower(getX() + X_OFFSET, getY() + Y_OFFSET), true);
		SuperMario.getInstance().getMapHolder().setComponentZOrder(this, 2);
	}
}