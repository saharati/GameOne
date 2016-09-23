package network.request;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Requesting to logout from the server.
 * @author Sahar
 */
public final class RequestLogout extends PacketWriter
{
	public static final RequestLogout STATIC_PACKET = new RequestLogout();
	
	private RequestLogout()
	{
		
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.LOGOUT.ordinal());
	}
}