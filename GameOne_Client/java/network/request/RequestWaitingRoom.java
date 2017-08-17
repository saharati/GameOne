package network.request;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

public final class RequestWaitingRoom extends PacketWriter
{
	private final GameId _gameId;
	
	public RequestWaitingRoom(final GameId gameId)
	{
		_gameId = gameId;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.WAIT.ordinal());
		
		writeInt(_gameId.ordinal());
	}
}