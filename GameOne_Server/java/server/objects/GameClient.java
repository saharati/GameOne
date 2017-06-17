package server.objects;

import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import network.BasicClient;
import network.PacketInfo;
import network.PacketReader;
import network.response.GameScoreUpdateResponse;
import network.response.MessageResponse;
import network.response.WaitingRoomResponse;
import objects.GameId;
import objects.GameResult;
import util.Broadcast;
import util.StringUtil;

/**
 * This class represents a game client attached to the server.
 * @author Sahar
 */
public final class GameClient extends BasicClient
{
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
		final String msg;
		if (user == null)
		{
			msg = _user.getUsername() + " has logged off.";
			
			final GameId currentGame = _user.getCurrentGame();
			final UserGroup group = _user.getGroup();
			
			_user.onLogout();
			
			if (currentGame != null)
			{
				if (group != null)
				{
					final GameScoreUpdateResponse result = new GameScoreUpdateResponse(GameResult.EXIT, currentGame);
					group.getUsersExcept(_user).forEach(member -> member.sendPacket(result));
				}
				
				Broadcast.toAllUsersOfGame(new WaitingRoomResponse(currentGame), currentGame);
			}
		}
		else
			msg = user.getUsername() + " has logged on.";
		
		final String logonMsg = StringUtil.refineBeforeSend("Server", msg);
		Broadcast.toAllExcept(logonMsg, this);
		
		LOGGER.info(msg);
		
		_user = user;
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
		_readBuffer.flip();
		while (_readBuffer.hasRemaining())
		{
			final int opCode = _readBuffer.getInt();
			final PacketInfo inf = PacketInfo.values()[opCode];
			final PacketReader<BasicClient> packet = inf.getReadPacket();
			packet.setBuffer(_readBuffer);
			packet.read();
			packet.run(this);
		}
		_readBuffer.clear();
		
		getChannel().read(_readBuffer, this, _readHandler);
	}
	
	@Override
	public void onDisconnect()
	{
		if (_user == null)
			LOGGER.info(_remoteAddr + " terminated the connection.");
		else
			setUser(null);
	}
}