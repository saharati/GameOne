package network.request;

import network.PacketInfo;
import network.PacketWriter;

public final class RequestGameConfigs extends PacketWriter
{
	public static final RequestGameConfigs STATIC_PACKET = new RequestGameConfigs();
	
	private RequestGameConfigs()
	{
		
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.CONFIGS.ordinal());
	}
}