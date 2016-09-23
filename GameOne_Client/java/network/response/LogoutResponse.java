package network.response;

import client.Client;
import network.PacketReader;
import windows.GameSelect;
import windows.Login;

/**
 * Packet indicates that logging out succeed.
 * @author Sahar
 */
public final class LogoutResponse extends PacketReader<Client>
{
	@Override
	public void read()
	{
		
	}
	
	@Override
	public void run(final Client client)
	{
		client.setCurrentWindow(Login.getInstance());
		
		GameSelect.getInstance().getChatWindow().setText("");
		GameSelect.getInstance().getSender().setText("");
	}
}