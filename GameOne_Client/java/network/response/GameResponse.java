package network.response;

import client.Client;
import network.PacketReader;
import objects.GameId;

/**
 * GameResponse packet implementation.
 * @author Sahar
 */
public final class GameResponse extends PacketReader<Client>
{
	private int _gameId;
	
	@Override
	public void read(final Client client)
	{
		_gameId = readInt();
	}
	
	@Override
	public void run(final Client client)
	{
		final GameId gameId = GameId.values()[_gameId];
		switch (gameId)
		{
			case CHECKERS:
				break;
			case CHESS:
				break;
			case G2048:
				break;
			case LAMA:
				break;
			case LOBBY:
				break;
			case MARIO:
				break;
			case PACMAN:
				break;
			case SNAKE:
				break;
			case TETRIS:
				break;
		}
	}
}