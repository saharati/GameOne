package network.response;

import network.PacketInfo;
import network.PacketWriter;
import objects.LoginResult;

public final class LoginResponse extends PacketWriter
{
	public static final LoginResponse LOGIN_OK = new LoginResponse(LoginResult.LOGIN_OK);
	public static final LoginResponse LOGIN_FAILED = new LoginResponse(LoginResult.LOGIN_FAILED);
	public static final LoginResponse SERVER_FULL = new LoginResponse(LoginResult.SERVER_FULL);
	public static final LoginResponse SERVER_ERROR = new LoginResponse(LoginResult.SERVER_ERROR);
	public static final LoginResponse ALREADY_ONLINE = new LoginResponse(LoginResult.ALREADY_ONLINE);
	public static final LoginResponse USER_BANNED = new LoginResponse(LoginResult.USER_BANNED);
	
	private final LoginResult _result;
	
	private LoginResponse(final LoginResult result)
	{
		_result = result;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.LOGIN.ordinal());
		
		writeInt(_result.ordinal());
	}
}