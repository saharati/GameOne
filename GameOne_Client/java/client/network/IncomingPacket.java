package client.network;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import network.PacketReader;

/**
 * Initial packet handler.
 * @author Sahar
 */
public final class IncomingPacket implements CompletionHandler<Integer, ConnectionManager>
{
	private static final Logger LOGGER = Logger.getLogger(IncomingPacket.class.getName());
	
	@Override
	public void completed(final Integer result, final ConnectionManager attachment)
	{
		final PacketReader reader = attachment.getReader();
		reader.getBuffer().flip();
		
		final int opCode = reader.readInt();
		final PacketInfo inf = PacketInfo.values()[opCode];
		final IIncomingPacket packet = inf.newIncomingPacket();
		packet.read(attachment, reader);
		packet.run(attachment);
		
		reader.getBuffer().clear();
		
		attachment.getChannel().read(reader.getBuffer(), attachment, this);
	}
	
	@Override
	public void failed(final Throwable exc, final ConnectionManager attachment)
	{
		try
		{
			if (attachment.getChannel().isOpen())
				attachment.getChannel().close();
		}
		catch (final IOException e)
		{
			LOGGER.log(Level.WARNING, "Error while closing connection: ", e);
		}
		
		// TODO What do we do when we get here as a client?
	}
}