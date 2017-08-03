package network.response;

import network.PacketInfo;
import network.PacketWriter;

public final class GameStartResponse extends PacketWriter
{
	private final boolean _isStarting;
	
	public GameStartResponse(final boolean isStarting)
	{
		_isStarting = isStarting;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.START.ordinal());
		
		writeBoolean(_isStarting);
	}
}