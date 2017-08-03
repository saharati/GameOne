package network.request;

import network.PacketReader;
import network.response.LogoutResponse;
import server.objects.GameClient;

public final class RequestLogout extends PacketReader<GameClient>
{
	@Override
	public void read()
	{
		
	}
	
	@Override
	public void run(final GameClient client)
	{
		client.setUser(null);
		client.sendPacket(LogoutResponse.STATIC_PACKET);
	}
}