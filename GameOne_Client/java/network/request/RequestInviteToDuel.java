package network.request;

import network.PacketInfo;
import network.PacketWriter;

public final class RequestInviteToDuel extends PacketWriter
{
	public static final RequestInviteToDuel STATIC_PACKET = new RequestInviteToDuel(null);
	
	private final String _targetName;
	
	public RequestInviteToDuel(final String targetName)
	{
		_targetName = targetName;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.INVITE.ordinal());
		
		writeString(_targetName);
	}
}