package client.network.incoming;

import client.Client;
import network.IIncomingPacket;
import network.PacketReader;
import windows.GameSelect;

/**
 * MessageResponse packet implementation.
 * @author Sahar
 */
public final class MessageResponse implements IIncomingPacket<Client>
{
	private String _message;
	
	@Override
	public void read(final Client client, final PacketReader packet)
	{
		_message = packet.readString();
	}
	
	@Override
	public void run(final Client client)
	{
		GameSelect.getInstance().getChatWindow().append(_message);
	}
}