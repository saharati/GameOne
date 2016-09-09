package server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import util.configs.Config;

/**
 * This class listens to client connections.
 * @author Sahar
 */
public final class ConnectionManager
{
	private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
	
	private static AsynchronousServerSocketChannel _asynchronousServerSocketChannel;
	
	public static void open()
	{
		try
		{
			final AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
			
			_asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(group);
			_asynchronousServerSocketChannel.bind(new InetSocketAddress(Config.PORT), 0);
			
			while (_asynchronousServerSocketChannel.isOpen())
			{
				final Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = _asynchronousServerSocketChannel.accept();
				final AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();
				final SocketAddress remoteAddress = asynchronousSocketChannel.getRemoteAddress();
				
				LOGGER.info("New incoming connection from: " + remoteAddress);
				
				new GameClient(asynchronousSocketChannel, remoteAddress);
			}
		}
		catch (final IOException | InterruptedException | ExecutionException e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static void close() throws IOException
	{
		if (_asynchronousServerSocketChannel.isOpen())
			_asynchronousServerSocketChannel.close();
	}
}