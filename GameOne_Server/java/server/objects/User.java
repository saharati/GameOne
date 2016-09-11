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
	private final boolean _isGM;
	private final GameClient _client;
	private final Map<Integer, GameStat> _gameStats = new HashMap<>();
	
	public User(final int id, final String name, final boolean isGM, final GameClient client)
	{
		_id = id;
		_name = name;
		_isGM = isGM;
		_client = client;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public boolean isGM()
	{
		return _isGM;
	}
	
	public GameClient getClient()
	{
		return _client;
	}
	
	public Map<Integer, GameStat> getGameStats()
	{
		return _gameStats;
	}
}