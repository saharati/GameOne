package network.response;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

public final class GameResponse extends PacketWriter
{
	private final GameId _gameId;
	private final boolean _joined;
	
	public GameResponse(final GameId gameId, final boolean joined)
	{
		_gameId = gameId;
		_joined = joined;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.GAME.ordinal());
		
		writeInt(_gameId.ordinal());
		writeBoolean(_joined);
	}
}