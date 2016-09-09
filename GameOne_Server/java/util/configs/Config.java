package util.configs;

import util.parsers.properties.ExProperties;

/**
 * Load all server configurations files.
 * @author Sahar
 */
public final class Config
{
	// --------------------------------------------------
	// Property File Definitions
	// --------------------------------------------------
	private static final String DEADLOCK_FILE = "./configs/Deadlock.properties";
	private static final String DATABASE_FILE = "./configs/Database.properties";
	private static final String SERVER_FILE = "./configs/Server.properties";
	private static final String NETWORK_FILE = "./configs/Network.properties";
	
	// --------------------------------------------------
	// Deadlock Settings
	// --------------------------------------------------
	public static boolean DEADLOCK_DETECTOR;
	public static boolean RESTART_ON_DEADLOCK;
	public static int DEADLOCK_CHECK_INTERVAL;
	
	// --------------------------------------------------
	// Database Settings
	// --------------------------------------------------
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MAX_CONNECTIONS;
	public static int DATABASE_MAX_IDLE_TIME;
	
	// --------------------------------------------------
	// Server Settings
	// --------------------------------------------------
	public static int MAXIMUM_ONLINE_USERS;
	public static boolean AUTO_CREATE_ACCOUNTS;
	
	// --------------------------------------------------
	// Network Settings
	// --------------------------------------------------
	public static boolean ENABLE_UPNP;
	public static int PORT;
	
	public static void load()
	{
		// Load Deadlock.properties file (if exists)
		final ExProperties deadlock = new ExProperties(DEADLOCK_FILE);
		
		DEADLOCK_DETECTOR = deadlock.getProperty("DeadLockDetector", true);
		RESTART_ON_DEADLOCK = deadlock.getProperty("RestartOnDeadlock", false);
		DEADLOCK_CHECK_INTERVAL = deadlock.getProperty("DeadLockCheckInterval", 20) * 1000;
		
		// Load Database.properties file (if exists)
		final ExProperties database = new ExProperties(DATABASE_FILE);
		
		DATABASE_URL = database.getProperty("URL", "jdbc:mysql://localhost/gameOne");
		DATABASE_LOGIN = database.getProperty("Login", "root");
		DATABASE_PASSWORD = database.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = database.getProperty("MaximumDbConnections", 10);
		DATABASE_MAX_IDLE_TIME = database.getProperty("MaximumDbIdleTime", 0);
		
		// Load Server.properties file (if exists)
		final ExProperties server = new ExProperties(SERVER_FILE);
		
		MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);
		AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", false);
		
		// Load Network.properties file (if exists)
		final ExProperties network = new ExProperties(NETWORK_FILE);
		
		ENABLE_UPNP = network.getProperty("EnableUPnP", true);
		PORT = network.getProperty("Port", 777);
	}
}