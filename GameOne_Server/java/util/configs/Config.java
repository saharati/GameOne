package util.configs;

import java.util.logging.Logger;

import util.parsers.PropertiesParser;

/**
 * Load all server configurations files.
 * @author Sahar
 */
public final class Config
{
	private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
	
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
	public static int DATABASE_MAX_CONNECTIONS;
	public static int DATABASE_MAX_IDLE_TIME;
	public static String MYSQL_URL;
	public static String MYSQL_LOGIN;
	public static String MYSQL_PASSWORD;
	public static String ACCESS_URL;
	public static String ACCESS_LOGIN;
	public static String ACCESS_PASSWORD;
	
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
		final PropertiesParser deadlock = new PropertiesParser(DEADLOCK_FILE);
		
		DEADLOCK_DETECTOR = deadlock.getProperty("DeadLockDetector", true);
		RESTART_ON_DEADLOCK = deadlock.getProperty("RestartOnDeadlock", false);
		DEADLOCK_CHECK_INTERVAL = deadlock.getProperty("DeadLockCheckInterval", 20) * 1000;
		
		// Load Database.properties file (if exists)
		final PropertiesParser database = new PropertiesParser(DATABASE_FILE);
		
		DATABASE_MAX_CONNECTIONS = database.getProperty("MaximumDbConnections", 10);
		DATABASE_MAX_IDLE_TIME = database.getProperty("MaximumDbIdleTime", 0);
		MYSQL_URL = database.getProperty("MysqlUrl", "jdbc:mysql://localhost/gameOne");
		MYSQL_LOGIN = database.getProperty("MysqlLogin", "root");
		MYSQL_PASSWORD = database.getProperty("MysqlPassword", "");
		ACCESS_URL = database.getProperty("AccessUrl", "jdbc:ucanaccess://gameOne.accdb;");
		ACCESS_LOGIN = database.getProperty("AccessLogin", "");
		ACCESS_PASSWORD = database.getProperty("AccessPassword", "");
		
		// Load Server.properties file (if exists)
		final PropertiesParser server = new PropertiesParser(SERVER_FILE);
		
		MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);
		AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", false);
		
		// Load Network.properties file (if exists)
		final PropertiesParser network = new PropertiesParser(NETWORK_FILE);
		
		ENABLE_UPNP = network.getProperty("EnableUPnP", true);
		PORT = network.getProperty("Port", 777);
		
		LOGGER.info("Config loaded!");
	}
}