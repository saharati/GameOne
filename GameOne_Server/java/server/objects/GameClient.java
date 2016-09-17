package server.objects;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Logger;

import network.BasicClient;
import network.PacketInfo;
import network.PacketReader;
import network.response.MessageResponse;
import util.StringUtil;

/**
 * This class represents a game client attached to the server.
 * @author Sahar
 */
public final class GameClient extends BasicClient
{
	private static final Logger LOGGER = Logger.getLogger(GameClient.class.getName());
	
	private final SocketAddress _remoteAddr;
	private User _user;
	
	public GameClient(final AsynchronousSocketChannel channel, final SocketAddress remoteAddress)
	{
		super.setChannel(channel);
		
		_remoteAddr = remoteAddress;
	}
	
	public SocketAddress getRemoteAddress()
	{
		return _remoteAddr;
	}
	
	public User getUser()
	{
		return _user;
	}
	
	public void setUser(final User user)
	{
		if (user == null)
			LOGGER.info("User: " + _user.getUsername() + " has logged off.");
		else
			LOGGER.info("User: " + user.getUsername() + " has logged on.");
		
		_user = user;
	}
	
	public boolean isAuthed()
	{
		return _user != null;
	}
	
	public void sendPacket(final String sender, final String msg)
	{
		final String refinedMsg = StringUtil.refineBeforeSend(sender, msg);
		final MessageResponse packet = new MessageResponse(refinedMsg);
		
		sendPacket(packet);
	}
	
	@Override
	public void readPacket()
	{
		final ByteBuffer buffer = getReadBuffer();
		buffer.flip();
		
		while (buffer.hasRemaining())
		{
			final int opCode = buffer.getInt();
			final PacketInfo inf = PacketInfo.values()[opCode];
			final PacketReader<BasicClient> packet = inf.getReadPacket();
			packet.setBuffer(buffer);
			packet.read(this);
			
			if (inf.isAuthedState() == isAuthed())
				packet.run(this);
		}
		buffer.clear();
		
		getChannel().read(buffer, this, getReadHandler());
	}
	
	@Override
	public void onDisconnect()
	{
		if (_user == null)
			LOGGER.info(_remoteAddr + " terminated the connection.");
		else
		{
			// TODO
			/*
			if (_user.isInDuel())
				new NotifyLogout(_user.getDuel().getOpponent().getThread());

			_objects.put("gameId", _user.getCurrentGameId());

			new WaitingRoomInfo(this);
			 */
			
			LOGGER.info("User: " + _user.getUsername() + " has logged off.");
		}
	}
}