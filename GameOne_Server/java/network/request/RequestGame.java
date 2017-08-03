package network.request;

import network.PacketReader;
import network.response.GameResponse;
import objects.GameId;
import server.objects.GameClient;

public final class RequestGame extends PacketReader<GameClient>
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
		if (_gameId == -1)
		{
			client.sendPacket(new GameResponse(client.getUser().getCurrentGame(), false));
			client.getUser().setCurrentGame(null);
		}
		else
		{
			final GameId gameId = GameId.values()[_gameId];
			
			client.getUser().setCurrentGame(gameId);
			client.sendPacket(new GameResponse(gameId, true));
		}
	}
}