package network.request;

import network.PacketReader;
import network.response.GameScoreUpdateResponse;
import network.response.WaitingRoomResponse;
import objects.GameId;
import objects.GameResult;
import server.objects.GameClient;
import server.objects.GameStat;
import server.objects.User;
import server.objects.UserGroup;
import util.Broadcast;

public final class RequestUpdateGameScore extends PacketReader<GameClient>
{
	private GameResult _result;
	private int _score;
	
	@Override
	public void read()
	{
		_result = GameResult.values()[readInt()];
		_score = readInt();
	}
	
	@Override
	public void run(final GameClient client)
	{
		// Part 1 - Update the current user.
		final User user = client.getUser();
		final GameId currentGame = user.getCurrentGame();
		final GameStat gs = user.hasGame(currentGame) ? user.getGame(currentGame) : new GameStat(currentGame);
		switch (_result)
		{
			case WIN:
			case EXIT:
				gs.setWins(gs.getWins() + 1);
				break;
			case LOSE:
			case LEAVE:
				gs.setLoses(gs.getLoses() + 1);
				break;
		}
		// Some games accumulate score, while others just try to reach a certain goal.
		switch (currentGame)
		{
			case PACMAN:
			case MARIO:
			case TETRIS:
			case SNAKE:
			case G2048:
			case CHESS_SP:
				gs.setScore(Math.max(_score, gs.getScore()));
				break;
			case CHESS_MP:
			case LAMA:
			case CHECKERS:
				switch (_result)
				{
					case WIN:
					case EXIT:
						gs.setScore(gs.getScore() + _score);
						break;
					case LOSE:
					case LEAVE:
						gs.setScore(gs.getScore() - _score);
						break;
				}
				break;
		}
		user.saveGameStat(gs);
		
		// Part 2 - Update group members if user is in group.
		if (user.isInGroup())
		{
			final UserGroup group = user.getGroup();
			if (group.getMembers().size() == 2)
			{
				final User otherUser = group.getUsersExcept(user).findFirst().get();
				final GameStat otherGs = otherUser.hasGame(currentGame) ? otherUser.getGame(currentGame) : new GameStat(currentGame);
				
				// Opposite of what original player gets.
				GameResult broadcastResult = _result;
				switch (_result)
				{
					case WIN:
					case EXIT:
						otherGs.setLoses(otherGs.getLoses() + 1);
						otherGs.setScore(otherGs.getScore() - _score);
						
						if (_result == GameResult.WIN)
							broadcastResult = GameResult.LOSE;
						else
							broadcastResult = GameResult.NONE;
						break;
					case LOSE:
					case LEAVE:
						otherGs.setWins(otherGs.getWins() + 1);
						otherGs.setScore(otherGs.getScore() + _score);
						
						if (_result == GameResult.LOSE)
							broadcastResult = GameResult.WIN;
						break;
				}
				otherUser.saveGameStat(otherGs);
				
				client.sendPacket(new GameScoreUpdateResponse(_result == GameResult.EXIT ? GameResult.LEAVE : GameResult.NONE, currentGame));
				
				if (otherUser.getClient() != null)
					otherUser.sendPacket(new GameScoreUpdateResponse(broadcastResult, currentGame));
			}
			else
			{
				// TODO not yet taken care of.
			}
			
			group.disband();
			
			Broadcast.toAllUsersOfGame(new WaitingRoomResponse(currentGame), currentGame);
		}
		else
			client.sendPacket(new GameScoreUpdateResponse(_result, currentGame));
	}
}