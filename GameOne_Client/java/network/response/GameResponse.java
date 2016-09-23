package network.response;

import client.Client;
import network.PacketReader;
import objects.GameId;
import pacman.MapBuilder;
import windows.GameSelect;

/**
 * Packet responsible for carrying out actions on a specific game request.
 * @author Sahar
 */
public final class GameResponse extends PacketReader<Client>
{
	private int _gameId;
	
	@Override
	public void read()
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
				GameSelect.getInstance().disableAllButtons();
				MapBuilder.getInstance().setVisible(true);
				break;
			case SNAKE:
				break;
			case TETRIS:
				break;
		}
	}
}