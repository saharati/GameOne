package mario.objects;

import java.awt.Point;

import mario.SuperMario;
import mario.MarioTaskManager;
import objects.mario.MarioType;
import util.Direction;
import util.random.Rnd;

/**
 * A breakable cube object.
 * @author Sahar
 */
public final class BreakableCube extends AbstractObject
{
	private static final long serialVersionUID = 2694706540045414476L;
	
	// x = (CubeWidth - CoinWidth) / 2, y = 41 (cubeWidth + 1).
	private static final Point COIN_SPAWN_OFFSET = new Point((40 - 27) / 2, 41);
	
	private int _amount;
	
	public BreakableCube(final int x, final int y)
	{
		super(x, y, MarioType.CUBE2);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		_amount = Rnd.get(5) + 3;
	}
	
	@Override
	protected void onMeetObject(final Direction dir)
	{
		if (_amount == 0)
			return;
		if (dir != Direction.ABOVE)
			return;
		
		_amount--;
		
		if (_amount == 0 && Rnd.get(100) < 33)
		{
			final Mushrum m = new Mushrum(getX(), getY() - 1);
			SuperMario.getInstance().addObject(m, true);
			MarioTaskManager.getInstance().add(m);
		}
		else
		{
			final Coin c = new Coin(getX() + COIN_SPAWN_OFFSET.x, getY() - COIN_SPAWN_OFFSET.y);
			SuperMario.getInstance().addObject(c, true);
			c.animate();
		}
		
		if (_amount == 0)
			deleteMe();
	}
	
	@Override
	protected void deleteMe()
	{
		super.deleteMe();
		
		SuperMario.getInstance().addObject(new Cube(getX(), getY()), true);
	}
}