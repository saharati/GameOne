package server.network.outgoing;

import network.PacketWriter;
import server.network.PacketInfo;

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
		// Broadcast packets should only be written once.
		if (!getBuffer().hasRemaining())
			return;
		
		writeInt(PacketInfo.MESSAGE.ordinal());
		
		writeString(_message);
	}
}