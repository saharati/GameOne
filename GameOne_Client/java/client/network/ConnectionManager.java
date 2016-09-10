package client.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import configs.Config;
import network.PacketReader;
import network.PacketWriter;

/**
 * This class initialize connection to server.
 * @author Sahar
 */
public final class ConnectionManager
{
	private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
	
	private AsynchronousSocketChannel _channel;
	private final PacketReader _reader = new PacketReader(ByteBuffer.allocateDirect(1024));
	
	private ConnectionManager()
	{
		try
		{
			_channel = AsynchronousSocketChannel.open();
			
			final SocketAddress serverAddr = new InetSocketAddress(Config.SERVER_IP, Config.PORT);
			_channel.connect(serverAddr).get();
			
			LOGGER.info("Succesfully connected to server.");
			
			_channel.read(_reader.getBuffer(), this, new IncomingPacket());
		}
		catch (final IOException | InterruptedException | ExecutionException e)
		{
			JOptionPane.showMessageDialog(null, "Server appears to be offline or incorrect IP/Port provided.", "Connection Error", JOptionPane.ERROR_MESSAGE);
			
			System.exit(0);
		}
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
	
	public static ConnectionManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final ConnectionManager INSTANCE = new ConnectionManager();
	}
}