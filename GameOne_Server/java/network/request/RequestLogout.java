package network.request;

import network.PacketReader;
import network.response.LogoutResponse;
import server.objects.GameClient;

/**
 * RequestLogout packet implementation.
 * @author Sahar
 */
public final class RequestLogout extends PacketReader<GameClient>
{
	@Override
	public void read(final GameClient client)
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