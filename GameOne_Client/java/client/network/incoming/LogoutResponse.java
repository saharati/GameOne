package client.network.incoming;

import client.Client;
import network.IIncomingPacket;
import network.PacketReader;
import windows.GameSelect;
import windows.Login;

/**
 * LogoutResponse packet implementation.
 * @author Sahar
 */
public final class LogoutResponse implements IIncomingPacket<Client>
{
	@Override
	public void read(final Client client, final PacketReader packet)
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