package network.request;

import network.PacketReader;
import network.response.GameObjectsResponse;
import objects.GameId;
import server.objects.GameClient;

/**
 * RequestGameObjects packet implementation.
 * @author Sahar
 */
public final class RequestGameObjects extends PacketReader<GameClient>
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
		client.sendPacket(new GameObjectsResponse(gameId));
	}
}