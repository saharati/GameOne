package objects;

/**
 * Class representing a single mario object.
 * @author Sahar
 */
public final class MarioObject
{
	private final int _x;
	private final int _y;
	private final String _type;
	
	public MarioObject(final int x, final int y, final String type)
	{
		_x = x;
		_y = y;
		_type = type;
	}
	
	public int getX()
	{
		return _x;
	}
	
	public int getY()
	{
		return _y;
	}
	
	public String getType()
	{
		return _type;
	}
}