package util.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.LogManager;

import util.parsers.properties.ExProperties;

/**
 * Load all server configurations files.
 * @author Sahar
 */
public final class CommonConfig
{
	// --------------------------------------------------
	// Property File Definitions
	// --------------------------------------------------
	private static final String THREADS_FILE = "./configs/Threads.properties";
	private static final String LOGS_FILE = "./configs/log.cfg";
	
	// --------------------------------------------------
	// Threads Settings
	// --------------------------------------------------
	public static int SCHEDULED_THREAD_POOL_COUNT;
	public static int THREADS_PER_SCHEDULED_THREAD_POOL;
	public static int INSTANT_THREAD_POOL_COUNT;
	public static int THREADS_PER_INSTANT_THREAD_POOL;
	
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
		final ExProperties threads = new ExProperties(threadFile);
		
		SCHEDULED_THREAD_POOL_COUNT = threads.getProperty("ScheduledThreadPoolCount", -1);
		THREADS_PER_SCHEDULED_THREAD_POOL = threads.getProperty("ThreadsPerScheduledThreadPool", 4);
		INSTANT_THREAD_POOL_COUNT = threads.getProperty("InstantThreadPoolCount", -1);
		THREADS_PER_INSTANT_THREAD_POOL = threads.getProperty("ThreadsPerInstantThreadPool", 2);
	}
}