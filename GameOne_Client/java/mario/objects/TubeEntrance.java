package mario.objects;

import javax.swing.SwingUtilities;

import mario.MarioScreen;
import objects.mario.MarioType;

/**
 * A entrance tube.
 * @author Sahar
 */
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
		
		SwingUtilities.invokeLater(() -> MarioScreen.getInstance().add(new Flower(getX() + X_OFFSET, getY() + Y_OFFSET)));
	}
}