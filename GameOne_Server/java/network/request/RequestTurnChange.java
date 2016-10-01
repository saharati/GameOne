package network.request;

import network.PacketReader;
import network.response.TurnChangeResponse;
import objects.GameId;
import server.objects.GameClient;

/**
 * Inform partner that now its his turn, depending on game.
 * @author Sahar
 */
public final class RequestTurnChange extends PacketReader<GameClient>
{
	private GameId _gameId;
	
	// Chess
	private String[] _images;
	private int[][] _positions;
	
	// Slide a Lama
	private String[][] _matrixPanel;
	private String[] _nextCards;
	private int _score;
	
	@Override
	public void read()
	{
		_gameId = GameId.values()[readInt()];
		switch (_gameId)
		{
			case CHESS:
				_images = new String[2];
				_positions = new int[2][4];
				for (int i = 0;i < _images.length;i++)
					_images[i] = readString();
				for (int i = 0;i < _positions.length;i++)
					for (int j = 0;j < _positions[i].length;j++)
						_positions[i][j] = readInt();
				break;
			case LAMA:
				_matrixPanel = new String[5][5];
				_nextCards = new String[3];
				for (int i = 0;i < _matrixPanel.length;i++)
					for (int j = 0;j < _matrixPanel[i].length;j++)
						_matrixPanel[i][j] = readString();
				for (int i = 0;i < _nextCards.length;i++)
					_nextCards[i] = readString();
				_score = readInt();
				break;
		}
	}
	
	@Override
	public void run(final GameClient client)
	{
		switch (_gameId)
		{
			case CHESS:
				client.getUser().getGroup().getUsersExcept(client.getUser()).findFirst().get().sendPacket(new TurnChangeResponse(_images, _positions));
				break;
			case LAMA:
				client.getUser().getGroup().getUsersExcept(client.getUser()).findFirst().get().sendPacket(new TurnChangeResponse(_matrixPanel, _nextCards, _score));
				break;
		}
	}
}