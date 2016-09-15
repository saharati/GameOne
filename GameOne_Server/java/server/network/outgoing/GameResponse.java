package server.network.outgoing;

import network.PacketWriter;
import objects.GameId;
import server.network.PacketInfo;

/**
 * Outgoing GameResponse packet implementation.
 * @author Sahar
 */
public final class GameResponse extends PacketWriter
{
	private final GameId _gameId;
	
	public GameResponse(final GameId gameId)
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