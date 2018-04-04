package util.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import data.AnnouncementsTable;
import data.MarioTable;
import data.PacmanTable;
import data.UsersTable;
import util.configs.Config;

public final class Database
{
	private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
	private static final String CHECK_DATABASE = "SELECT COUNT(*) c FROM information_schema.tables WHERE table_schema=?";
	
	private static ComboPooledDataSource _source;
	
	public static void setSource(final ComboPooledDataSource source)
	{
		_source = source;
	}
	
	public static ComboPooledDataSource getSource()
	{
		return _source;
	}
	
	public static void checkInstallation() throws SQLException, IOException
	{
		try (final Connection con = getConnection();
			final PreparedStatement ps = con.prepareStatement(CHECK_DATABASE))
		{
			ps.setString(1, Config.MYSQL_DB_NAME);
			try (final ResultSet rs = ps.executeQuery())
			{
				if  (!rs.next() || rs.getInt("c") == 0)
				{
					try (final Statement s = con.createStatement())
					{
						LOGGER.info("Installing Database...");
						
						final List<File> files = Files.list(Paths.get("./sql")).filter(path -> path.toString().endsWith(".sql")).map(Path::toFile).collect(Collectors.toList());
						for (final File file : files)
						{
							LOGGER.info("Installing " + file.getName());
							
							try (final Scanner sc = new Scanner(file))
							{
								final StringBuilder sb = new StringBuilder();
								while (sc.hasNextLine())
								{
									String line = sc.nextLine();
									if (line.startsWith("--"))
										continue;
									
									if (line.contains("--"))
										line = line.split("--")[0];
									
									line = line.trim();
									if (!line.isEmpty())
										sb.append(line + System.getProperty("line.separator"));
									
									if (line.endsWith(";"))
									{
										s.execute(sb.toString());
										
										sb.setLength(0);
									}
								}
							}
						}
						
						LOGGER.info("Database installation complete.");
					}
				}
			}
		}
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