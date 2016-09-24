package mario.objects;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import mario.MarioBuilder;
import mario.MarioScreen;
import mario.TaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * Abstract class for every mario object.
 * @author Sahar
 */
public abstract class AbstractObject extends JLabel
{
	private static final long serialVersionUID = 4977156251835827143L;
	
	private ImageIcon[] _images;
	
	protected AbstractObject(final int x, final int y, final MarioType... types)
	{
		setImages(types);
		setBounds(x, y, _images[0].getIconWidth(), _images[0].getIconHeight());
	}
	
	public final ImageIcon[] getImages()
	{
		return _images;
	}
	
	public final Map<Direction, List<AbstractObject>> getNearbyObjects(final Rectangle bounds)
	{
		final Map<Direction, List<AbstractObject>> res = new HashMap<>();
		res.put(Direction.ABOVE, new ArrayList<>());
		res.put(Direction.BELOW, new ArrayList<>());
		res.put(Direction.RIGHT, new ArrayList<>());
		res.put(Direction.LEFT, new ArrayList<>());
		
		for (final AbstractObject target : MarioScreen.getInstance().getObjects())
		{
			if (target == this)
				continue;
			if (target.skip())
				continue;
			
			if (bounds.intersects(target.getBounds()))
			{
				// During a jump, priorities change.
				if (this instanceof Player && ((Player) this).isJumping())
				{
					if (isLeft(target))
						res.get(Direction.RIGHT).add(target);
					else if (isRight(target))
						res.get(Direction.LEFT).add(target);
					else if (isAbove(target))
						res.get(Direction.BELOW).add(target);
					else if (isBelow(target))
						res.get(Direction.ABOVE).add(target);
				}
				else
				{
					if (isAbove(target))
						res.get(Direction.BELOW).add(target);
					else if (isBelow(target))
						res.get(Direction.ABOVE).add(target);
					else if (isLeft(target))
						res.get(Direction.RIGHT).add(target);
					else if (isRight(target))
						res.get(Direction.LEFT).add(target);
				}
			}
		}
		
		return res;
	}
	
	public void notifyTimeOut()
	{
		// Nothing
	}
	
	protected final void setImages(final MarioType... types)
	{
		_images = new ImageIcon[types.length];
		for (int i = 0;i < types.length;i++)
			_images[i] = MarioBuilder.getInstance().getAllImages().get(types[i].getImage());
		
		setIcon(_images[0]);
	}
	
	protected final void setImages(final ImageIcon... images)
	{
		_images = new ImageIcon[images.length];
		for (int i = 0;i < images.length;i++)
			_images[i] = images[i];
		
		setIcon(_images[0]);
	}
	
	protected boolean skip()
	{
		return false;
	}
	
	protected boolean canGoThrough()
	{
		return false;
	}
	
	protected void onMeetObject(final Direction dir)
	{
		// Nothing
	}
	
	protected void deleteMe()
	{
		MarioScreen.getInstance().getObjects().remove(this);
		TaskManager.getInstance().remove(this);
	}
	
	private boolean isLeft(final AbstractObject target)
	{
		return getBounds().getMaxX() - 2 < target.getX();
	}
	
	private boolean isAbove(final AbstractObject target)
	{
		return getBounds().getMaxY() - 2 < target.getY();
	}
	
	private boolean isRight(final AbstractObject target)
	{
		return target.getBounds().getMaxX() - 2 < getX();
	}
	
	private boolean isBelow(final AbstractObject target)
	{
		return target.getBounds().getMaxY() - 2 < getY();
	}
}