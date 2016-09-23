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
	
	public static final MapObject EMPTY = new MapObject(-128, -128, PacmanObject.EMPTY);
	
	private final int _initialX;
	private final int _initialY;
	private final PacmanObject _initialType;
	
	private PacmanObject _type;
	private PacmanObject _reservedType;
	
	public MapObject(final int x, final int y, final PacmanObject type)
	{
		_initialX = x;
		_initialY = y;
		_initialType = type;
		
		setBounds(x, y, 64, 64);
		setBackground(Color.BLACK);
		setType(type);
	}
	
	public PacmanObject getType()
	{
		return _type;
	}
	
	public void setType(final PacmanObject type)
	{
		if (type == PacmanObject.MOB_SLOW)
			_reservedType = _type;
		else if (type == PacmanObject.EMPTY)
			setVisible(false);
		
		_type = type;
		
		repaint();
	}
	
	public PacmanObject getReservedType()
	{
		return _reservedType;
	}
	
	public void reset()
	{
		setLocation(_initialX, _initialY);
		setType(_initialType);
		setVisible(true);
	}
	
	@Override
	public void paintComponent(final Graphics g)
	{
		g.drawImage(MapBuilder.getInstance().getPacmanObjects().get(_type), 0, 0, 64, 64, Color.BLACK, null);
	}
}