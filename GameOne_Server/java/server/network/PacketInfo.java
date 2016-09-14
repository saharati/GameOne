package server.network;

import java.util.function.Supplier;

import network.IIncomingPacket;
import server.network.incoming.*;
import server.objects.GameClient;

/**
 * List of all possible packets.
 * @author Sahar
 */
public enum PacketInfo
{
	LOGIN(RequestLogin::new, false),
	MESSAGE(RequestMessage::new, true),
	LOGOUT(RequestLogout::new, true);
	
	private final Supplier<IIncomingPacket<GameClient>> _incomingPacketFactory;
	private final boolean _authed;
	
	private PacketInfo(final Supplier<IIncomingPacket<GameClient>> incomingPacketFactory, final boolean authed)
	{
		_incomingPacketFactory = incomingPacketFactory;
		_authed = authed;
	}
	
	public IIncomingPacket<GameClient> newIncomingPacket()
	{
		return _incomingPacketFactory.get();
	}
	
	public boolean isAuthedState()
	{
		return _authed;
	}
}