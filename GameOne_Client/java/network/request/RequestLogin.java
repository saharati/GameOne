package network.request;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Request a login from the server with the given information.
 * @author Sahar
 */
public final class RequestLogin extends PacketWriter
{
	private final String _username;
	private final String _password;
	private final String _mac;
	
	public RequestLogin(final String username, final String password, final String mac)
	{
		_username = username;
		_password = password;
		_mac = mac;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.LOGIN.ordinal());
		
		writeString(_username);
		writeString(_password);
		writeString(_mac);
	}
}