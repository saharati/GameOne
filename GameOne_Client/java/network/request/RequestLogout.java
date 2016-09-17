package network.request;

import network.PacketInfo;
import network.PacketWriter;

/**
 * RequestLogout packet implementation.
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