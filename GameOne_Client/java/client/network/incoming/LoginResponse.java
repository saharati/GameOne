package client.network.incoming;

import java.util.logging.Logger;

import client.network.ConnectionManager;
import client.network.IIncomingPacket;
import network.PacketReader;

/**
 * LoginResponse packet implementation.
 * @author Sahar
 */
public final class LoginResponse implements IIncomingPacket
{
	private static final Logger LOGGER = Logger.getLogger(LoginResponse.class.getName());
	
	private byte _result;
	
	@Override
	public void read(final ConnectionManager client, final PacketReader packet)
	{
		_result = packet.readByte();
	}
	
	@Override
	public void run(final ConnectionManager client)
	{
		
	}
}