package network.response;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Packet responsible for telling the result of a specific map edit request.
 * @author Sahar
 */
public final class PacmanMapEditResponse extends PacketWriter
{
	public static final PacmanMapEditResponse NO_PERMISSION = new PacmanMapEditResponse((byte) -1);
	public static final PacmanMapEditResponse FAIL = new PacmanMapEditResponse((byte) -2);
	public static final PacmanMapEditResponse SUCCESS = new PacmanMapEditResponse((byte) 1);
	
	private final byte _result;
	
	private PacmanMapEditResponse(final byte result)
	{
		_result = result;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.PACMAN_EDIT.ordinal());
		
		writeByte(_result);
	}
}