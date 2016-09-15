package data.sql.objects;

/**
 * Class representing a single mario object.
 * @author Sahar
 */
public final class MarioObject
{
	private final String _type;
	private final int _x;
	private final int _y;
	
	public MarioObject(final String type, final int x, final int y)
	{
		_type = type;
		_x = x;
		_y = y;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public int getX()
	{
		return _x;
	}
	
	public int getY()
	{
		return _y;
	}
}