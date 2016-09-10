package client.network;

import java.util.function.Supplier;

import client.network.incoming.*;

/**
 * List of all possible packets.
 * @author Sahar
 */
public enum PacketInfo
{
	LOGIN(LoginResponse::new),
	MESSAGE(MessageResponse::new);
	
	private final Supplier<IIncomingPacket> _incomingPacketFactory;
	
	private PacketInfo(final Supplier<IIncomingPacket> incomingPacketFactory)
	{
		_incomingPacketFactory = incomingPacketFactory;
	}
	
	public IIncomingPacket newIncomingPacket()
	{
		return _incomingPacketFactory.get();
	}
}