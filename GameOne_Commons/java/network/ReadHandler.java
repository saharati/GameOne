package network;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handler used for reading packets.
 * @author Sahar
 * @param <T> The client type being used.
 */
public final class ReadHandler<T extends BasicClient> implements CompletionHandler<Integer, T>
{
	private static final Logger LOGGER = Logger.getLogger(ReadHandler.class.getName());
	
	@Override
	public void completed(final Integer result, final T attachment)
	{
		attachment.readPacket();
	}
	
	@Override
	public void failed(final Throwable exc, final T attachment)
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
		
		attachment.onDisconnect();
	}
}