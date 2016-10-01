package network.request;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Request a specific multiplayer game to start.
 * @author Sahar
 */
public final class RequestGameStart extends PacketWriter
{
	public static final RequestGameStart STATIC_PACKET = new RequestGameStart();
	
	private RequestGameStart()
	{
		
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.START.ordinal());
	}
}