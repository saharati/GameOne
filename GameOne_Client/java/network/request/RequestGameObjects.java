package network.request;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

public final class RequestGameObjects extends PacketWriter
{
	private final GameId _gameId;
	
	public RequestGameObjects(final GameId gameId)
	{
		_gameId = gameId;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.OBJECTS.ordinal());
		
		writeInt(_gameId.ordinal());
	}
}