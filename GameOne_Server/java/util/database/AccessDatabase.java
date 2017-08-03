package util.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import util.configs.Config;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AccessDatabase
{
	private static final Logger LOGGER = Logger.getLogger(AccessDatabase.class.getName());
	private static final ComboPooledDataSource SOURCE = new ComboPooledDataSource();
	
	public static void load() throws PropertyVetoException, SQLException
	{
		SOURCE.setAutoCommitOnClose(true);
		
		SOURCE.setInitialPoolSize(10);
		SOURCE.setMinPoolSize(10);
		SOURCE.setMaxPoolSize(Math.max(10, Config.DATABASE_MAX_CONNECTIONS));
		
		SOURCE.setAcquireRetryAttempts(0); // Try to obtain connections indefinitely (0 = never quit).
		SOURCE.setAcquireRetryDelay(500); // 500 milliseconds wait before try to acquire connection again.
		SOURCE.setCheckoutTimeout(0); // 0 = wait indefinitely for new connection if pool is exhausted.
		SOURCE.setAcquireIncrement(5); // If pool is exhausted, get 5 more connections at a time, its faster than getting 1 by 1.
		
		SOURCE.setIdleConnectionTestPeriod(3600); // Test idle connection every 60 seconds.
		SOURCE.setMaxIdleTime(Config.DATABASE_MAX_IDLE_TIME); // 0 = idle connections never expire.
		
		SOURCE.setMaxStatementsPerConnection(100); // Enables statement caching.
		
		SOURCE.setBreakAfterAcquireFailure(false); // false = avoid generating FATAL errors, so program will not crash.
		
		SOURCE.setDriverClass("net.ucanaccess.jdbc.UcanaccessDriver");
		SOURCE.setJdbcUrl(Config.ACCESS_URL);
		SOURCE.setUser(Config.ACCESS_LOGIN);
		SOURCE.setPassword(Config.ACCESS_PASSWORD);
		
		SOURCE.getConnection().close(); // Test the connection.
		
		LOGGER.info("Access database loaded.");
	}
	
	public static void shutdown()
	{
		SOURCE.close();
	}
	
	public static ComboPooledDataSource getSource()
	{
		return SOURCE;
	}
	
	public static Connection getConnection()
	{
		while (true)
		{
			try
			{
				return SOURCE.getConnection();
			}
			catch (final SQLException e)
			{
				LOGGER.log(Level.WARNING, "Database: getConnection() failed, trying again ", e);
			}
		}
	}
}