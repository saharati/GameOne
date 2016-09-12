package network;

import network.PacketReader;

/**
 * Interface for incoming packet implementation.
 * @author Sahar
 */
public interface IIncomingPacket<T extends BasicClient>
{
	public void read(final T client, final PacketReader packet);
	
	public void run(final T client);
}