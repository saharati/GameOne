package network.response;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

/**
 * Packet responsible for telling the result of a specific game edit request.
 * @author Sahar
 */
public final class GameEditResponse extends PacketWriter
{
	public static final byte NO_PERMISSION = -1;
	public static final byte FAIL = -2;
	public static final byte SUCCESS = 1;
	
	private final GameId _gameId;
	private final byte _result;
	
	public GameEditResponse(final GameId gameId, final byte result)
	{
		_gameId = gameId;
		_result = result;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.EDIT.ordinal());
		
		writeInt(_gameId.ordinal());
		writeByte(_result);
	}
}