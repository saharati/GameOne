package network.request;

import network.PacketReader;
import network.response.GameConfigsResponse;
import server.objects.GameClient;

/**
 * Packet respoinsble for requesting game configs.
 * @author Sahar
 */
public final class RequestGameConfigs extends PacketReader<GameClient>
{
	@Override
	public void read()
	{
		
	}
	
	@Override
	public void run(final GameClient client)
	{
		client.sendPacket(new GameConfigsResponse());
	}
}