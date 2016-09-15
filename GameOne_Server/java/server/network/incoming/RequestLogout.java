package server.network.incoming;

import network.IIncomingPacket;
import network.PacketReader;
import server.network.outgoing.LogoutResponse;
import server.objects.GameClient;

/**
 * RequestLogout packet implementation.
 * @author Sahar
 */
public final class RequestLogout implements IIncomingPacket<GameClient>
{
	@Override
	public void read(final GameClient client, final PacketReader packet)
	{
		
	}
	
	@Override
	public void run(final GameClient client)
	{
		client.getUser().onLogout();
		client.setUser(null);
		client.sendPacket(LogoutResponse.STATIC_PACKET);
	}
}