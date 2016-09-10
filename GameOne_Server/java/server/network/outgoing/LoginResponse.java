package server.network.outgoing;

import network.PacketWriter;
import server.network.PacketInfo;

/**
 * Outgoing Login packet implementation.
 * @author Sahar
 */
public final class LoginResponse extends PacketWriter
{
	public static final LoginResponse LOGIN_OK = new LoginResponse((byte) 1);
	public static final LoginResponse LOGIN_FAILED = new LoginResponse((byte) -1);
	public static final LoginResponse SERVER_FULL = new LoginResponse((byte) -2);
	public static final LoginResponse SERVER_ERROR = new LoginResponse((byte) -3);
	
	private final byte _result;
	
	private LoginResponse(final byte result)
	{
		_result = result;
	}
	
	@Override
	public void write()
	{
		// Static packets should be written only once at initialization.
		if (!getBuffer().hasRemaining())
			return;
		
		writeInt(PacketInfo.LOGIN.ordinal());
		
		writeByte(_result);
	}
}