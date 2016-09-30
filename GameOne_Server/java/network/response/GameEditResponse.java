package network.response;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Packet responsible for telling the result of a specific game edit request.
 * @author Sahar
 */
public final class GameEditResponse extends PacketWriter
{
	public static final GameEditResponse NO_PERMISSION = new GameEditResponse((byte)-1);
	public static final GameEditResponse FAIL = new GameEditResponse((byte) -2);
	public static final GameEditResponse SUCCESS = new GameEditResponse((byte) 1);
	
	private final byte _result;
	
	private GameEditResponse(final byte result)
	{
		_result = result;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.EDIT.ordinal());
		
		writeByte(_result);
	}
}