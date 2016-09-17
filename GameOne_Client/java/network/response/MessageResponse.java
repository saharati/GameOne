package network.response;

import client.Client;
import network.PacketReader;
import windows.GameSelect;

/**
 * MessageResponse packet implementation.
 * @author Sahar
 */
public final class MessageResponse extends PacketReader<Client>
{
	private String _message;
	
	@Override
	public void read(final Client client)
	{
		_message = readString();
	}
	
	@Override
	public void run(final Client client)
	{
		GameSelect.getInstance().getChatWindow().append(_message);
	}
}