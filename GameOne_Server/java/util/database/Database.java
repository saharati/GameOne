package util.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import data.AnnouncementsTable;
import data.MarioTable;
import data.PacmanTable;
import data.UsersTable;

public final class Database
{
	private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
	
	private static ComboPooledDataSource _source;
	
	public static void setSource(final ComboPooledDataSource source)
	{
		_source = source;
	}
	
	public static ComboPooledDataSource getSource()
	{
		return _source;
	}
	
	public static void syncData()
	{
		LOGGER.info("Data sync with Access source...");
		AnnouncementsTable.getInstance().sync();
		UsersTable.getInstance().sync();
		PacmanTable.getInstance().sync();
		MarioTable.getInstance().sync();
		LOGGER.info("Data sync finished.");
	}
	
	public static Connection getConnection()
	{
		while (true)
		{
			try
			{
				return _source.getConnection();
			}
			catch (final SQLException e)
			{
				LOGGER.log(Level.WARNING, "Database: getConnection() failed, trying again ", e);
			}
		}
	}
}