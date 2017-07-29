package util.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import util.parsers.PropertiesParser;

public final class CommonConfig
{
	private static final Logger LOGGER = Logger.getLogger(CommonConfig.class.getName());
	
	// --------------------------------------------------
	// Property File Definitions
	// --------------------------------------------------
	private static final String THREADS_FILE = "./configs/Threads2.properties";
	private static final String LOGS_FILE = "./configs/Log.properties";
	
	// --------------------------------------------------
	// Threads Settings
	// --------------------------------------------------
	public static int SCHEDULED_THREAD_POOL_SIZE;
	public static int INSTANT_THREAD_POOL_SIZE;
	
	public static void load() throws URISyntaxException, SecurityException, IOException
	{
		new File("./log").mkdir();
		
		File logFile = new File(LOGS_FILE);
		File threadFile;
		// It exists only when running compiled version.
		if (logFile.exists())
			threadFile = new File(THREADS_FILE);
		// Doesn't exist when running through source.
		else
		{
			final String classFile = CommonConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			final String classDir = new File(classFile).getParentFile().getAbsolutePath();
			
			logFile = new File(classDir + LOGS_FILE);
			threadFile = new File(classDir + THREADS_FILE);
		}
		
		try (final InputStream is = new FileInputStream(logFile))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		// Load Threads.properties file (if exists)
		final PropertiesParser threads = new PropertiesParser(threadFile);
		
		SCHEDULED_THREAD_POOL_SIZE = threads.getProperty("ScheduledThreadPoolSize", -1);
		INSTANT_THREAD_POOL_SIZE = threads.getProperty("InstantThreadPoolSize", -1);
		
		LOGGER.info("CommonConfig loaded!");
	}
}