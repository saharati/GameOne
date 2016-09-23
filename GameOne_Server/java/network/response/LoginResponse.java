package network.response;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Packet responsible for telling the client the login status.
 * @author Sahar
 */
public final class LoginResponse extends PacketWriter
{
	public static final LoginResponse LOGIN_OK = new LoginResponse((byte) 1);
	public static final LoginResponse LOGIN_FAILED = new LoginResponse((byte) -1);
	public static final LoginResponse SERVER_FULL = new LoginResponse((byte) -2);
	public static final LoginResponse SERVER_ERROR = new LoginResponse((byte) -3);
	public static final LoginResponse ALREADY_ONLINE = new LoginResponse((byte) -4);
	public static final LoginResponse USER_BANNED = new LoginResponse((byte) -5);
	
	private final byte _result;
	
	private LoginResponse(final byte result)
	{
		_result = result;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.LOGIN.ordinal());
		
		writeByte(_result);
	}
}