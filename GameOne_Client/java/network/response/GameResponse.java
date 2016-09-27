package network.response;

import client.Client;
import mario.SuperMario;
import network.PacketReader;
import objects.GameId;
import pacman.PacmanBuilder;
import s2048.S2048;
import snake.SnakeScreen;
import tetris.TetrisScreen;
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
		GameSelect.getInstance().disableAllButtons();
		
		final GameId gameId = GameId.values()[_gameId];
		switch (gameId)
		{
			case CHECKERS:
				break;
			case CHESS:
				break;
			case G2048:
				S2048.getInstance().start();
				break;
			case LAMA:
				break;
			case LOBBY:
				break;
			case MARIO:
				SuperMario.getInstance().setVisible(true);
				break;
			case PACMAN:
				PacmanBuilder.getInstance().setVisible(true);
				break;
			case SNAKE:
				SnakeScreen.getInstance().start();
				break;
			case TETRIS:
				TetrisScreen.getInstance().start();
				break;
		}
	}
}