package network.response;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Outgoing MessageResponse packet implementation.
 * @author Sahar
 */
public final class MessageResponse extends PacketWriter
{
	private final String _message;
	
	public MessageResponse(final String message)
	{
		_message = message;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.MESSAGE.ordinal());
		
		writeString(_message);
	}
}