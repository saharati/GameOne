package network;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class WriteHandler implements CompletionHandler<Integer, BasicClient>
{
	private static final Logger LOGGER = Logger.getLogger(WriteHandler.class.getName());
	
	@Override
	public void completed(final Integer result, final BasicClient attachment)
	{
		attachment.setPendingWrite(false);
		attachment.executeWriteTask();
	}
	
	@Override
	public void failed(final Throwable exc, final BasicClient attachment)
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