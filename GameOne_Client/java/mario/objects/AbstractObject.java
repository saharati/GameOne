package mario.objects;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import mario.SuperMario;
import mario.MarioTaskManager;
import mario.prototypes.Direction;
import objects.mario.MarioType;

/**
 * Abstract class for every mario object.
 * @author Sahar
 */
public abstract class AbstractObject extends JLabel
{
	private static final long serialVersionUID = 4977156251835827143L;
	
	private final MarioType[] _initialTypes;
	private final int _initialX;
	private final int _initialY;
	
	private MarioType[] _types;
	
	protected AbstractObject(final int x, final int y, final MarioType... types)
	{
		_initialTypes = types;
		_initialX = x;
		_initialY = y;
		
		setTypes(_initialTypes);
		setBounds(_initialX, _initialY, _types[0].getIcon().getIconWidth(), _types[0].getIcon().getIconHeight());
		setDoubleBuffered(true);
	}
	
	public final Map<Direction, List<AbstractObject>> getNearbyObjects(final Rectangle bounds)
	{
		final Map<Direction, List<AbstractObject>> res = new HashMap<>();
		res.put(Direction.ABOVE, new ArrayList<>());
		res.put(Direction.BELOW, new ArrayList<>());
		res.put(Direction.RIGHT, new ArrayList<>());
		res.put(Direction.LEFT, new ArrayList<>());
		
		for (final AbstractObject target : SuperMario.getInstance().getObjects())
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
	
	protected final MarioType[] getTypes()
	{
		return _types;
	}
	
	protected final void setTypes(final MarioType... types)
	{
		_types = types;
		
		setIcon(_types[0].getIcon());
	}
	
	/**
	 * Runs when user clicks the Play button on SelectionPanel.
	 */
	public void onStart()
	{
		// Nothing
	}
	
	/**
	 * Runs when player dies.
	 */
	public void onEnd()
	{
		setTypes(_initialTypes);
		setBounds(_initialX, _initialY, _types[0].getIcon().getIconWidth(), _types[0].getIcon().getIconHeight());
		
		if (!isVisible())
			setVisible(true);
	}
	
	public void notifyTimeOut()
	{
		// Nothing
	}
	
	/**
	 * @return {@code true} if this object should be treated like it doesn't even exist on the map, {@code false} otherwise.
	 */
	protected boolean skip()
	{
		return !isVisible();
	}
	
	/**
	 * @return {@code true} if this object should not stop another object's movement upon collision, {@code false} otherwise.
	 */
	protected boolean canGoThrough()
	{
		return !isVisible();
	}
	
	protected void onMeetObject(final Direction dir)
	{
		// Nothing
	}
	
	protected void deleteMe()
	{
		MarioTaskManager.getInstance().remove(this);
		
		setVisible(false);
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