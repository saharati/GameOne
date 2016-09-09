package util.configs;

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
	
	// --------------------------------------------------
	// Threads Settings
	// --------------------------------------------------
	public static int SCHEDULED_THREAD_POOL_COUNT;
	public static int THREADS_PER_SCHEDULED_THREAD_POOL;
	public static int INSTANT_THREAD_POOL_COUNT;
	public static int THREADS_PER_INSTANT_THREAD_POOL;
	
	public static void load()
	{
		// Load Threads.properties file (if exists)
		final ExProperties threads = new ExProperties(THREADS_FILE);
		
		SCHEDULED_THREAD_POOL_COUNT = threads.getProperty("ScheduledThreadPoolCount", -1);
		THREADS_PER_SCHEDULED_THREAD_POOL = threads.getProperty("ThreadsPerScheduledThreadPool", 4);
		INSTANT_THREAD_POOL_COUNT = threads.getProperty("InstantThreadPoolCount", -1);
		THREADS_PER_INSTANT_THREAD_POOL = threads.getProperty("ThreadsPerInstantThreadPool", 2);
	}
}