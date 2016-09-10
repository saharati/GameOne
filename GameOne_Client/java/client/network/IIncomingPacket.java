package client.network;

import network.PacketReader;

/**
 * Interface for incoming packet implementation.
 * @author Sahar
 */
public interface IIncomingPacket
{
	public void read(final ConnectionManager client, final PacketReader packet);
	
	public void run(final ConnectionManager client);
}