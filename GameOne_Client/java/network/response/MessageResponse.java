package network.response;

import client.Client;
import network.PacketReader;
import windows.GameSelect;

/**
 * Incoming chat message from the server.
 * @author Sahar
 */
public final class MessageResponse extends PacketReader<Client>
{
	private String _message;
	
	@Override
	public void read()
	{
		_message = readString();
	}
	
	@Override
	public void run(final Client client)
	{
		GameSelect.getInstance().getChatWindow().append(_message);
	}
}