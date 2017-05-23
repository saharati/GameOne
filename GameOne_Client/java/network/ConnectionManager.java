package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import client.Client;
import configs.Config;

/**
 * This class initialize connection to server.
 * @author Sahar
 */
public final class ConnectionManager
{
	private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
	
	@SuppressWarnings("resource")
	public static void open()
	{
		try
		{
			final AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
			final SocketAddress serverAddr = new InetSocketAddress(Config.SERVER_IP, Config.PORT);
			channel.connect(serverAddr).get();
			
			LOGGER.info("Succesfully connected to server.");
			
			Client.getInstance().setChannel(channel);
		}
		catch (final IOException | InterruptedException | ExecutionException e)
		{
			LOGGER.log(Level.WARNING, "Failed opening connection: ", e);
			
			JOptionPane.showMessageDialog(null, "Server appears to be offline or incorrect IP/Port provided.", "Connection Error", JOptionPane.ERROR_MESSAGE);
			
			System.exit(0);
		}
	}
}