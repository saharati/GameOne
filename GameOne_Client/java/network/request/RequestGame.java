package network.request;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

/**
 * RequestGame packet implementation.
 * @author Sahar
 */
public final class RequestGame extends PacketWriter
{
	private final GameId _gameId;
	
	public RequestGame(final GameId gameId)
	{
		_gameId = gameId;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.GAME.ordinal());
		
		writeInt(_gameId.ordinal());
	}
}