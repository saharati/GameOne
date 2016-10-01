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
		}
	}
}