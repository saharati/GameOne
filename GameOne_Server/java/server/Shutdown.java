package server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import network.ConnectionManager;
import util.Broadcast;
import util.StringUtil;
import util.UPnPService;
import util.database.AccessDatabase;
import util.database.MysqlDatabase;
import util.threadpool.ThreadPool;

public final class Shutdown
{
	private enum ShutdownMode
	{
		SIGTERM("SIGTERM"),
		SHUTDOWN("shutting down"),
		RESTART("restarting");
		
		private final String _text;
		
		private ShutdownMode(final String text)
		{
			_text = text;
		}
		
		@Override
		public String toString()
		{
			return _text;
		}
	}
	
	protected static final Logger LOGGER = Logger.getLogger(Shutdown.class.getName());
	
	protected ShutdownMode _shutdownMode;
	protected int _shutdownSeconds;
	
	protected Shutdown()
	{
		_shutdownMode = ShutdownMode.SIGTERM;
		_shutdownSeconds = -1;
	}
	
	public void startShutdown(final String activator, final int seconds, final boolean restart)
	{
		if (_shutdownMode != ShutdownMode.SIGTERM)
		{
			LOGGER.warning(activator + " requested a server shutdown while it is already in shutdown procedure.");
			return;
		}
		
		_shutdownMode = restart ? ShutdownMode.RESTART : ShutdownMode.SHUTDOWN;
		_shutdownSeconds = seconds;
		
		LOGGER.warning(activator + " issued shutdown command, " + _shutdownMode + " in " + seconds + " seconds.");
		
		if (_shutdownMode != ShutdownMode.SIGTERM)
		{
			switch (seconds)
			{
				case 540:
				case 480:
				case 420:
				case 360:
				case 300:
				case 240:
				case 180:
				case 120:
				case 60:
				case 30:
				case 10:
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
					// Handled by runnable.
					break;
				default:
					final String msg = StringUtil.refineBeforeSend("Server", "The server will be coming down in " + seconds + " seconds!");
					Broadcast.toAllUsers(msg);
					break;
			}
		}
		
		final ShutdownTask st = new ShutdownTask();
		Runtime.getRuntime().addShutdownHook(st);
		
		st.start();
	}
	
	public void abort(final String activator)
	{
		if (_shutdownMode != ShutdownMode.SIGTERM)
		{
			LOGGER.warning(activator + " issued shutdown abort, " + _shutdownMode + " has been stopped.");
			
			final String msg = StringUtil.refineBeforeSend("Server", "Server aborts " + _shutdownMode + " and continues normal operation.");
			Broadcast.toAllUsers(msg);
			
			_shutdownMode = ShutdownMode.SIGTERM;
		}
	}
	
	protected class ShutdownTask extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				while (_shutdownSeconds > 0)
				{
					switch (_shutdownSeconds)
					{
						case 540:
						case 480:
						case 420:
						case 360:
						case 300:
						case 240:
						case 180:
						case 120:
						case 60:
						case 30:
						case 10:
						case 5:
						case 4:
						case 3:
						case 2:
						case 1:
							final String msg = StringUtil.refineBeforeSend("Server", "Coming down in " + _shutdownSeconds + " seconds!");
							Broadcast.toAllUsers(msg);
							break;
					}
					
					_shutdownSeconds--;
					
					Thread.sleep(1000);
					
					// Shutdown was aborted, quit the task.
					if (_shutdownMode == ShutdownMode.SIGTERM)
						return;
				}
			}
			catch (final InterruptedException e)
			{
				
			}
			
			StringUtil.printSection("Under " + _shutdownMode + " process.");
			
			try
			{
				UPnPService.removePorts();
				LOGGER.info("UPnP Service: All port mappings deleted.");
			}
			catch (final IOException | SAXException e)
			{
				LOGGER.log(Level.WARNING, "UPnP Service: Failed deleting port mappings: ", e);
			}
			
			try
			{
				ConnectionManager.getInstance().close();
				LOGGER.info("ConnectionManager: Connection closed.");
			}
			catch (final IOException e)
			{
				LOGGER.log(Level.WARNING, "ConnectionManager: Failed closing connection: ", e);
			}
			
			ThreadPool.shutdown();
			LOGGER.info("ThreadPool: Shut down.");
			
			MysqlDatabase.shutdown();
			AccessDatabase.shutdown();
			LOGGER.info("Database: Connection closed.");
			
			// Server will quit, when this function ends.
			if (_shutdownMode == ShutdownMode.RESTART)
			{
				Runtime.getRuntime().halt(2);
				System.exit(2);
			}
			else
			{
				Runtime.getRuntime().halt(0);
				System.exit(0);
			}
		}
	}
	
	public static Shutdown getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final Shutdown INSTANCE = new Shutdown();
	}
}