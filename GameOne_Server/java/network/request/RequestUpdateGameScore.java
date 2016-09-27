package network.request;

import network.PacketReader;
import network.response.GameScoreUpdateResponse;
import objects.GameId;
import server.objects.GameClient;
import server.objects.GameStat;

/**
 * Packet responsible updating a user's score data.
 * @author Sahar
 */
public final class RequestUpdateGameScore extends PacketReader<GameClient>
{
	private boolean _isWin;
	private int _totalScore;
	
	@Override
	public void read()
	{
		_isWin = readBoolean();
		_totalScore = readInt();
	}
	
	@Override
	public void run(final GameClient client)
	{
		final GameId currentGame = client.getUser().getCurrentGame();
		if (currentGame == null || currentGame == GameId.LOBBY)
			return;
		
		if (!client.getUser().hasGame(currentGame))
		{
			final GameStat gs = new GameStat(currentGame, _totalScore, _isWin ? 1 : 0, !_isWin ? 1 : 0);
			
			client.getUser().saveGameStat(gs);
		}
		else
		{
			final GameStat gs = client.getUser().getGame(currentGame);
			
			// Some games accumulate score, while others just try to reach a certain goal.
			switch (currentGame)
			{
				case PACMAN:
				case MARIO:
				case TETRIS:
				case SNAKE:
					gs.setScore(Math.max(_totalScore, gs.getScore()));
					break;
			}
			
			if (_isWin)
				gs.setWins(gs.getWins() + 1);
			else
				gs.setLoses(gs.getLoses() + 1);
			
			client.getUser().saveGameStat(gs);
		}
		
		client.sendPacket(new GameScoreUpdateResponse(currentGame));
	}
}