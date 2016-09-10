package server.objects;

/**
 * Class holding records of a certain user.
 * @author Sahar
 */
public final class GameStat
{
	private final int _gameId;
	
	private int _score;
	private int _wins;
	private int _loses;
	
	public GameStat(final int gameId, final int score, final int wins, final int loses)
	{
		_gameId = gameId;
		_score = score;
		_wins = wins;
		_loses = loses;
	}
	
	public int getGameId()
	{
		return _gameId;
	}
	
	public int getScore()
	{
		return _score;
	}
	
	public void setScore(final int score)
	{
		_score = score;
	}
	
	public int getWins()
	{
		return _wins;
	}
	
	public void setWins(final int wins)
	{
		_wins = wins;
	}
	
	public int getLoses()
	{
		return _loses;
	}
	
	public void setLoses(final int loses)
	{
		_loses = loses;
	}
}