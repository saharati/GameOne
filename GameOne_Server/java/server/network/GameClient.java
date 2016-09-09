package server.network;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Logger;

import server.network.incoming.IncomingPacket;
import server.network.incoming.PacketReader;
import server.network.outgoing.OutgoingPacket;
import server.objects.User;
import util.Broadcast;

/**
 * This class represents a game client attached to the server.
 * @author Sahar
 */
public final class GameClient
{
	public static final int BUFFER_SIZE = 1024; // 1kb, change according to program's needs.
	
	private static final Logger LOGGER = Logger.getLogger(GameClient.class.getName());
	
	private final AsynchronousSocketChannel _asynchronousSocketChannel;
	private final PacketReader _reader = new PacketReader(ByteBuffer.allocateDirect(BUFFER_SIZE));
	private final SocketAddress _remoteAddr;
	
	private User _user;
	 
	public GameClient(final AsynchronousSocketChannel asynchronousSocketChannel, final SocketAddress remoteAddress)
	{
		_asynchronousSocketChannel = asynchronousSocketChannel;
		_asynchronousSocketChannel.read(_reader.getBuffer(), this, new IncomingPacket());
		
		_remoteAddr = remoteAddress;
		
		Broadcast.THREADS.add(this);
	}
	
	public AsynchronousSocketChannel getAsynchronousSocketChannel()
	{
		return _asynchronousSocketChannel;
	}
	
	public PacketReader getReader()
	{
		return _reader;
	}
	
	public SocketAddress getRemoteAddress()
	{
		return _remoteAddr;
	}
	
	public void sendPacket(OutgoingPacket packet)
	{
		_asynchronousSocketChannel.write(packet.getBuffer());
	}
	
	public User getUser()
	{
		return _user;
	}
	
	public void setUser(User user)
	{
		_user = user;
		
		LOGGER.info("User: " + user.getName() + " has logged on.");
	}
	
	public boolean isAuthed()
	{
		return _user != null;
	}
}