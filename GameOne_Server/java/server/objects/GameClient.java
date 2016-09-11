package server.objects;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Logger;

import network.PacketReader;
import network.PacketWriter;
import server.network.IncomingPacket;
import server.network.outgoing.MessageResponse;
import util.Broadcast;
import util.StringUtil;

/**
 * This class represents a game client attached to the server.
 * @author Sahar
 */
public final class GameClient
{
	private static final Logger LOGGER = Logger.getLogger(GameClient.class.getName());
	
	private final AsynchronousSocketChannel _asynchronousSocketChannel;
	private final PacketReader _reader = new PacketReader(ByteBuffer.allocateDirect(1024));
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
	
	public void sendPacket(final PacketWriter packet)
	{
		packet.write();
		packet.pack();
		
		_asynchronousSocketChannel.write(packet.getBuffer());
	}
	
	public void sendPacket(final String sender, final String msg)
	{
		final String refinedMsg = StringUtil.refineBeforeSend(sender, msg);
		final MessageResponse packet = new MessageResponse(refinedMsg);
		
		sendPacket(packet);
	}
	
	public User getUser()
	{
		return _user;
	}
	
	public void setUser(final User user)
	{
		_user = user;
		
		LOGGER.info("User: " + user.getName() + " has logged on.");
	}
	
	public boolean isAuthed()
	{
		return _user != null;
	}
}