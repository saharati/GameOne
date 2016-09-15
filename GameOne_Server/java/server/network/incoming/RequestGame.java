package server.network.incoming;

import network.IIncomingPacket;
import network.PacketReader;
import objects.GameId;
import server.network.outgoing.GameResponse;
import server.objects.GameClient;

/**
 * RequestMessage packet implementation.
 * @author Sahar
 */
public final class RequestGame implements IIncomingPacket<GameClient>
{
	private int _gameId;
	
	@Override
	public void read(final GameClient client, final PacketReader packet)
	{
		_gameId = packet.readInt();
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