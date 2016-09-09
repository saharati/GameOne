package server.network;

import java.util.function.Supplier;

import server.network.incoming.IIncomingPacket;
import server.network.incoming.impl.RequestLogin;

/**
 * List of all possible packets.
 * @author Sahar
 */
public enum PacketInfo
{
	LOGIN(RequestLogin::new, false);
	
	private final Supplier<IIncomingPacket> _incomingPacketFactory;
	private final boolean _authed;
	
	private PacketInfo(final Supplier<IIncomingPacket> incomingPacketFactory, final boolean authed)
	{
		_incomingPacketFactory = incomingPacketFactory;
		_authed = authed;
	}
	
	public IIncomingPacket newIncomingPacket()
	{
		return _incomingPacketFactory.get();
	}
	
	public boolean isAuthedState()
	{
		return _authed;
	}
}