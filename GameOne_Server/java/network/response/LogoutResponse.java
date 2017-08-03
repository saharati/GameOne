package network.response;

import network.PacketInfo;
import network.PacketWriter;

public final class LogoutResponse extends PacketWriter
{
	public static final LogoutResponse STATIC_PACKET = new LogoutResponse();
	
	private LogoutResponse()
	{
		
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.LOGOUT.ordinal());
	}
}