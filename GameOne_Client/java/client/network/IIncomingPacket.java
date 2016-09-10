package client.network;

import client.Client;
import network.PacketReader;

/**
 * Interface for incoming packet implementation.
 * @author Sahar
 */
public interface IIncomingPacket
{
	public void read(final Client client, final PacketReader packet);
	
	public void run(final Client client);
}