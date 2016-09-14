package server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.objects.GameClient;
import util.configs.Config;

/**
 * This class listens to client connections.
 * @author Sahar
 */
public final class ConnectionManager implements CompletionHandler<AsynchronousSocketChannel, Void>
{
	private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
	
	private final AsynchronousServerSocketChannel _asynchronousServerSocketChannel;
	
	private ConnectionManager()
	{
		try
		{
			_asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			_asynchronousServerSocketChannel.bind(new InetSocketAddress(Config.PORT), 0);
		}
		catch (final IOException e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public void listen()
	{
		_asynchronousServerSocketChannel.accept(null, this);
	}
	
	public void close() throws IOException
	{
		if (_asynchronousServerSocketChannel.isOpen())
			_asynchronousServerSocketChannel.close();
	}
	
	@Override
	public void completed(final AsynchronousSocketChannel result, final Void attachment)
	{
		try
		{
			final SocketAddress remoteAddress = result.getRemoteAddress();
			LOGGER.info("New incoming connection from: " + remoteAddress);
			
			new GameClient(result, remoteAddress);
		}
		catch (final IOException e)
		{
			LOGGER.log(Level.WARNING, "Failed getting remote address from new connection: ", e);
		}
		
		listen();
	}
	
	@Override
	public void failed(final Throwable exc, final Void attachment)
	{
		// Don't care if the error was due to connection closed (a.k.a server shutdown/restart).
		if (exc instanceof AsynchronousCloseException)
			return;
		
		LOGGER.log(Level.WARNING, "Failure on server socket: ", exc);
	}
	
	public static ConnectionManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final ConnectionManager INSTANCE = new ConnectionManager();
	}
}