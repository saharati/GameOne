package network.response;

import network.PacketInfo;
import network.PacketWriter;

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