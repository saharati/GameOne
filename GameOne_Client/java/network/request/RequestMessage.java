package network.request;

import network.PacketInfo;
import network.PacketWriter;

/**
 * RequestMessage packet implementation.
 * @author Sahar
 */
public final class RequestMessage extends PacketWriter
{
	private final String _message;
	
	public RequestMessage(final String message)
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