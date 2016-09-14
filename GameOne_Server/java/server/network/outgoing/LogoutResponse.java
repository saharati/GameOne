package server.network.outgoing;

import network.PacketWriter;
import server.network.PacketInfo;

/**
 * Outgoing LogoutResponse packet implementation.
 * @author Sahar
 */
public final class LogoutResponse extends PacketWriter
{
	public static final LogoutResponse STATIC_PACKET = new LogoutResponse();
	
	private LogoutResponse()
	{
		
	}
	
	@Override
	public void write()
	{
		// Static packets should be written only once at initialization.
		if (!getBuffer().hasRemaining())
			return;
		
		writeInt(PacketInfo.LOGOUT.ordinal());
	}
}