package network.request;

import network.PacketReader;
import network.response.GameResponse;
import objects.GameId;
import server.objects.GameClient;

/**
 * RequestMessage packet implementation.
 * @author Sahar
 */
public final class RequestGame extends PacketReader<GameClient>
{
	private int _gameId;
	
	@Override
	public void read(final GameClient client)
	{
		_gameId = readInt();
	}
	
	@Override
	public void run(final GameClient client)
	{
		if (GameId.values().length < _gameId)
			return;
		
		final GameId gameId = GameId.values()[_gameId];
		client.getUser().setCurrentGame(gameId);
		client.sendPacket(new GameResponse(gameId));
	}
}