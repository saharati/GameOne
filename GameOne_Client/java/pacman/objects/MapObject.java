package pacman.objects;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import objects.pacman.PacmanObject;
import pacman.MapBuilder;

/**
 * Class represting an object on the map.
 * @author Sahar
 */
public final class MapObject extends JPanel
{
	private static final long serialVersionUID = -8275383126819103107L;
	
	public static final MapObject EMPTY = new MapObject(PacmanObject.EMPTY);
	
	private final PacmanObject _initialType;
	
	private PacmanObject _type;
	private PacmanObject _reservedType;
	
	public MapObject(final PacmanObject type)
	{
		_initialType = type;
		
		setPreferredSize(MapBuilder.PIXEL_DIMENSIONS);
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
		
		g.drawImage(MapBuilder.getInstance().getPacmanObjects().get(_type), 0, 0, MapBuilder.BLOCK_SIZE, MapBuilder.BLOCK_SIZE, Color.BLACK, null);
	}
}