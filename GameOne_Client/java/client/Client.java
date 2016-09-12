package client;

import java.nio.ByteBuffer;
import java.util.prefs.Preferences;

import client.network.PacketInfo;
import network.BasicClient;
import network.IIncomingPacket;

/**
 * Class holding info regarding client such as current socket, records, user etc.
 * @author Sahar
 */
public final class Client extends BasicClient
{
	private String _username;
	private String _password;
	
	public void setLoginDetails(final String username, final String password)
	{
		_username = username;
		_password = password;
	}
	
	public void storeUserDetails()
	{
		final Preferences prop = Preferences.userRoot();
		
		prop.put("user_gameOne", _username);
		prop.put("pass_gameOne", _password);
	}
	
	@Override
	public void readPacket()
	{
		final ByteBuffer buffer = getReader().getBuffer();
		buffer.flip();
		
		while (buffer.hasRemaining())
		{
			final int opCode = getReader().readInt();
			final PacketInfo inf = PacketInfo.values()[opCode];
			final IIncomingPacket<Client> packet = inf.newIncomingPacket();
			packet.read(this, getReader());
			packet.run(this);
		}
		buffer.clear();
		
		getChannel().read(buffer, this, getReadHandler());
	}
	
	@Override
	public void onDisconnect()
	{
		// TODO What do we do when we get here as a client?
	}
	
	private Client() {}
	
	public static Client getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final Client INSTANCE = new Client();
	}
}