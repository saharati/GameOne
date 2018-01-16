package server;

import java.awt.Toolkit;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import data.AnnouncementsTable;
import data.MarioTable;
import data.PacmanTable;
import data.UsersTable;
import handlers.AdminCommandHandler;
import network.ConnectionManager;
import util.DeadLockDetector;
import util.StringUtil;
import util.UPnPService;
import util.configs.CommonConfig;
import util.configs.Config;
import util.configs.GameConfig;
import util.configs.IPConfig;
import util.database.AccessDatabase;
import util.database.Database;
import util.database.MysqlDatabase;
import util.parsers.XmlFactory;
import util.threadpool.ThreadPool;

public final class Startup
{
	private static final Logger LOGGER = Logger.getLogger(Startup.class.getName());
	
	public static void main(final String[] args) throws SecurityException, URISyntaxException, IOException, PropertyVetoException, SQLException, ParserConfigurationException
	{
		CommonConfig.load();
		Config.load();
		GameConfig.load();
		IPConfig.load();
		
		StringUtil.printSection("Parsers");
		XmlFactory.load();
		
		StringUtil.printSection("Database");
		AccessDatabase.load();
		try (final Socket mysql = new Socket("127.0.0.1", 3306))
		{
			MysqlDatabase.load();
			Database.setSource(MysqlDatabase.getSource());
			Database.checkInstallation();
		}
		catch (final IOException e)
		{
			LOGGER.warning("Warning: MySQL is not running on current machine, a backup database will be used.");
			LOGGER.warning("While using the backup database, no information will be stored!");
			
			Database.setSource(AccessDatabase.getSource());
		}
		AnnouncementsTable.getInstance();
		UsersTable.getInstance();
		PacmanTable.getInstance();
		MarioTable.getInstance();
		if (Database.getSource() == MysqlDatabase.getSource())
			Database.syncData();
		
		StringUtil.printSection("ThreadPool");
		ThreadPool.load();
		
		StringUtil.printSection("Handlers");
		LOGGER.info("Loaded " + AdminCommandHandler.getInstance().size() + " admin commands.");
		
		StringUtil.printSection("Network");
		UPnPService.openPorts();
		ConnectionManager.getInstance().listen();
		
		StringUtil.printSection("System");
		Shutdown.getInstance();
		
		if (Config.DEADLOCK_DETECTOR)
		{
			LOGGER.info("Deadlock detector is enabled, timer: " + Config.DEADLOCK_CHECK_INTERVAL + "ms.");
			
			final DeadLockDetector deadDetectThread = new DeadLockDetector();
			deadDetectThread.setDaemon(true);
			deadDetectThread.start();
		}
		else
			LOGGER.info("Deadlock detector is disabled.");
		
		Runtime.getRuntime().gc();
		
		final long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
		final long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		
		LOGGER.info("Server have started, used memory: " + usedMem + " / " + totalMem + " Mb.");
		LOGGER.info("Server loaded in " + (ManagementFactory.getRuntimeMXBean().getUptime() / 1000) + " seconds.");
		LOGGER.info("Maximum allowed players: " + Config.MAXIMUM_ONLINE_USERS + ".");
		
		Toolkit.getDefaultToolkit().beep();
	}
}