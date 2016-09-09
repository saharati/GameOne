package server;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import server.network.ConnectionManager;
import server.network.UPnPService;
import util.DeadLockDetector;
import util.StringUtil;
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
	
	public static void main(final String[] args) throws FileNotFoundException, IOException
	{
		new File("./log").mkdir();
		try (final InputStream is = new FileInputStream(new File("configs/log.cfg")))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		StringUtil.printSection("GameOne Server - Sahar Atias");
		
		StringUtil.printSection("Configs");
		IPConfig.load();
		Config.load();
		
		StringUtil.printSection("Parsers");
		XmlFactory.load();
		
		StringUtil.printSection("Database");
		Database.load();
		
		StringUtil.printSection("ThreadPool");
		ThreadPool.load();
		
		StringUtil.printSection("Network");
		UPnPService.openPorts();
		ConnectionManager.open();
		
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