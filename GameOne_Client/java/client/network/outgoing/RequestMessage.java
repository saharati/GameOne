package client.network.outgoing;

import client.network.PacketInfo;
import network.PacketWriter;

/**
 * RequestMessage packet implementation.
 * @author Sahar
 */
public final class RequestMessage extends PacketWriter
{
	private String _message;
	
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