package server;

import java.awt.Toolkit;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

import data.sql.AnnouncementsTable;
import data.sql.UsersTable;
import handlers.AdminCommandHandler;
import server.network.ConnectionManager;
import util.DeadLockDetector;
import util.StringUtil;
import util.UPnPService;
import util.configs.CommonConfig;
import util.configs.Config;
import util.configs.IPConfig;
import util.database.Database;
import util.parsers.xml.XmlFactory;
import util.threadpool.ThreadPool;

/**
 * This starts the whole thing :)
 * @author Sahar
 */
public final class Startup
{
	private static final Logger LOGGER = Logger.getLogger(Startup.class.getName());
	
	public static void main(final String[] args) throws Exception
	{
		CommonConfig.load();
		IPConfig.load();
		Config.load();
		
		StringUtil.printSection("Parsers");
		XmlFactory.load();
		
		StringUtil.printSection("Database");
		Database.load();
		AnnouncementsTable.getInstance();
		UsersTable.getInstance();
		
		StringUtil.printSection("ThreadPool");
		ThreadPool.load();
		
		StringUtil.printSection("Handlers");
		LOGGER.info("Loaded " + AdminCommandHandler.getInstance().size() + " admin commands.");
		
		StringUtil.printSection("Network");
		UPnPService.openPorts();
		ConnectionManager.getInstance().listen();
		
		StringUtil.printSection("System");
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		if (Config.DEADLOCK_DETECTOR)
		{
			LOGGER.info("Deadlock detector is enabled. Timer: " + Config.DEADLOCK_CHECK_INTERVAL + "ms.");
			
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
		LOGGER.info("Maximum allowed players: " + Config.MAXIMUM_ONLINE_USERS);
		
		Toolkit.getDefaultToolkit().beep();
	}
}