package server.network.incoming;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.network.GameClient;
import server.network.PacketInfo;
import util.Broadcast;

/**
 * Initial packet handler.
 * @author Sahar
 */
public final class IncomingPacket implements CompletionHandler<Integer, GameClient>
{
	private static final Logger LOGGER = Logger.getLogger(IncomingPacket.class.getName());
	
	@Override
	public void completed(final Integer result, final GameClient attachment)
	{
		final PacketReader reader = attachment.getReader();
		final int opCode = reader.readInt();
		final PacketInfo inf = PacketInfo.values()[opCode];
		if (inf.isAuthedState() != attachment.isAuthed())
		{
			final IllegalStateException err = new IllegalStateException(attachment.getRemoteAddress() + " tried to send packet " + inf + " in an unsuitable state.");
			
			failed(err, attachment);
			return;
		}
		
		final IIncomingPacket packet = inf.newIncomingPacket();
		
		packet.read(attachment, reader);
		packet.run(attachment);
		
		reader.getBuffer().clear();
		
		attachment.getAsynchronousSocketChannel().read(reader.getBuffer(), attachment, this);
	}

	@Override
	public void failed(final Throwable exc, final GameClient attachment)
	{
		// TODO AsynchronousCloseException ?
		
		LOGGER.log(Level.WARNING, "Error during communication, disconnecting user! ", exc);
		
		Broadcast.THREADS.remove(this);
		
		try
		{
			if (attachment.getAsynchronousSocketChannel().isOpen())
				attachment.getAsynchronousSocketChannel().close();
		}
		catch (final IOException e)
		{
			LOGGER.log(Level.WARNING, "Error while closing connection: ", e);
		}
		
		if (attachment.getUser() == null)
			LOGGER.info("A connection has been terminated.");
		else
		{
			// TODO
			/*
			if (_user.isInDuel())
				new NotifyLogout(_user.getDuel().getOpponent().getThread());

			_objects.put("gameId", _user.getCurrentGameId());

			new WaitingRoomInfo(this);
			 */
			LOGGER.info("User: " + attachment.getUser().getName() + " has logged off.");
		}
	}
}