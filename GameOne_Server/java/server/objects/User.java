package server.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a user associated to GameClient.
 * @author Sahar
 */
public final class User
{
	private final int _id;
	private final String _name;
	private final Map<Integer, GameStat> _gameStats = new HashMap<>();
	
	public User(final int id, final String name)
	{
		_id = id;
		_name = name;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public Map<Integer, GameStat> getGameStats()
	{
		return _gameStats;
	}
}