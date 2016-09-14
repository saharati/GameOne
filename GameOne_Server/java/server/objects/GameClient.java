package server.objects;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Logger;

import data.sql.AnnouncementsTable;
import network.BasicClient;
import network.IIncomingPacket;
import server.network.PacketInfo;
import server.network.outgoing.MessageResponse;
import util.Broadcast;
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
		
		Broadcast.THREADS.add(this);
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
			LOGGER.info("User: " + _user.getName() + " has logged off.");
		else
			LOGGER.info("User: " + user.getName() + " has logged on.");
		
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
	
	public void onEnter()
	{
		final String logonMsg = StringUtil.refineBeforeSend("Server", getUser().getName() + " has logged on.");
		Broadcast.toAllUsersExcept(logonMsg, getUser().getClient());
		
		AnnouncementsTable.getInstance().showAnnouncements(getUser().getClient());
		
		if (getUser().isGM())
		{
			getUser().getClient().sendPacket("Server", "You have admin priviliges.");
			getUser().getClient().sendPacket("Server", "Type //list for available commands.");
		}
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
			final IIncomingPacket<GameClient> packet = inf.newIncomingPacket();
			packet.read(this, getReader());
			
			if (inf.isAuthedState() == isAuthed())
				packet.run(this);
		}
		buffer.clear();
		
		getChannel().read(buffer, this, getReadHandler());
	}
	
	@Override
	public void onDisconnect()
	{
		Broadcast.THREADS.remove(this);
		
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
			
			LOGGER.info("User: " + _user.getName() + " has logged off.");
		}
	}
}