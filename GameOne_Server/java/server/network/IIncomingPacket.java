package server.network;

import network.PacketReader;
import server.objects.GameClient;

/**
 * Interface for incoming packet implementation.
 * @author Sahar
 */
public interface IIncomingPacket
{
	public void read(final GameClient client, final PacketReader packet);
	
	public void run(final GameClient client);
}