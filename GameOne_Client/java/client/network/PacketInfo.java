package client.network;

import java.util.function.Supplier;

import client.Client;
import client.network.incoming.*;
import network.IIncomingPacket;

/**
 * List of all possible packets.
 * @author Sahar
 */
public enum PacketInfo
{
	LOGIN(LoginResponse::new),
	MESSAGE(MessageResponse::new),
	LOGOUT(LogoutResponse::new),
	GAME(GameResponse::new);
	
	private final Supplier<IIncomingPacket<Client>> _incomingPacketFactory;
	
	private PacketInfo(final Supplier<IIncomingPacket<Client>> incomingPacketFactory)
	{
		_incomingPacketFactory = incomingPacketFactory;
	}
	
	public IIncomingPacket<Client> newIncomingPacket()
	{
		return _incomingPacketFactory.get();
	}
}