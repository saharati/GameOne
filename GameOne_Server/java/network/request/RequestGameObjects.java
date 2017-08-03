package network.request;

import network.PacketReader;
import network.response.GameObjectsResponse;
import objects.GameId;
import server.objects.GameClient;

public final class RequestGameObjects extends PacketReader<GameClient>
{
	private int _gameId;
	
	@Override
	public void read()
	{
		_gameId = readInt();
	}
	
	@Override
	public void run(final GameClient client)
	{
		final GameId gameId = GameId.values()[_gameId];
		client.sendPacket(new GameObjectsResponse(gameId));
	}
}