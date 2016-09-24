package pacman.objects;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import objects.pacman.PacmanObject;
import pacman.PacmanBuilder;

/**
 * Class represting an object on the map.
 * @author Sahar
 */
public final class PacmanMapObject extends JPanel
{
	private static final long serialVersionUID = -8275383126819103107L;
	
	public static final PacmanMapObject EMPTY = new PacmanMapObject(PacmanObject.EMPTY);
	
	private final PacmanObject _initialType;
	
	private PacmanObject _type;
	private PacmanObject _reservedType;
	
	public PacmanMapObject(final PacmanObject type)
	{
		_initialType = type;
		
		setPreferredSize(PacmanBuilder.PIXEL_DIMENSIONS);
		setBackground(Color.BLACK);
		setType(type);
	}
	
	public PacmanObject getType()
	{
		return _type;
	}
	
	public void setType(final PacmanObject type)
	{
		if (type.isSlow())
			_reservedType = _type;
		
		_type = type;
		
		repaint();
	}
	
	public PacmanObject getReservedType()
	{
		return _reservedType;
	}
	
	public void reset()
	{
		setType(_initialType);
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		g.drawImage(PacmanBuilder.getInstance().getPacmanObjects().get(_type), 0, 0, PacmanBuilder.BLOCK_SIZE, PacmanBuilder.BLOCK_SIZE, Color.BLACK, this);
	}
}