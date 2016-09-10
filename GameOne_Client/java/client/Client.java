package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.prefs.Preferences;

import client.network.IncomingPacket;
import network.PacketReader;
import network.PacketWriter;

/**
 * Class holding info regarding client such as current socket, records, user etc.
 * @author Sahar
 */
public final class Client
{
	private String _username;
	private String _password;
	private AsynchronousSocketChannel _channel;
	private final PacketReader _reader = new PacketReader(ByteBuffer.allocateDirect(1024));
	
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
	
	public void setChannel(final AsynchronousSocketChannel channel) throws IOException
	{
		if (_channel != null && _channel.isOpen())
			_channel.close();
		
		_channel = channel;
		_channel.read(_reader.getBuffer(), this, new IncomingPacket());
	}
	
	public AsynchronousSocketChannel getChannel()
	{
		return _channel;
	}
	
	public PacketReader getReader()
	{
		return _reader;
	}
	
	public void sendPacket(final PacketWriter packet)
	{
		packet.write();
		packet.pack();
		
		_channel.write(packet.getBuffer());
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