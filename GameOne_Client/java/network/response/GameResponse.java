package network.response;

import chess.ChessScreen;
import client.Client;
import network.PacketReader;
import network.request.RequestWaitingRoom;
import objects.GameId;
import s2048.S2048;
import snake.SnakeScreen;
import tetris.TetrisScreen;
import util.random.Rnd;
import windows.GameSelect;

public final class GameResponse extends PacketReader<Client>
{
	private GameId _gameId;
	private boolean _joined;
	
	@Override
	public void read()
	{
		_gameId = GameId.values()[readInt()];
		_joined = readBoolean();
	}
	
	@Override
	public void run(final Client client)
	{
		if (_joined)
		{
			GameSelect.getInstance().disableAllButtons();
			
			switch (_gameId)
			{
				case CHECKERS:
				case CHESS_MP:
				case LAMA:
					Client.getInstance().sendPacket(new RequestWaitingRoom(_gameId));
					break;
				case MARIO:
				case PACMAN:
					Client.getInstance().getCurrentWindow().setVisible(true);
					break;
				case G2048:
					S2048.getInstance().start();
					break;
				case SNAKE:
					SnakeScreen.getInstance().start();
					break;
				case TETRIS:
					TetrisScreen.getInstance().start();
					break;
				case CHESS_SP:
					ChessScreen.getInstance().start(Rnd.nextBoolean() ? "white" : "black", true);
					break;
			}
		}
		else
		{
			GameSelect.getInstance().enableAllButtons();
			
			switch (_gameId)
			{
				case CHECKERS:
				case CHESS_MP:
				case LAMA:
					Client.getInstance().sendPacket(new RequestWaitingRoom(_gameId));
					break;
			}
		}
	}
}