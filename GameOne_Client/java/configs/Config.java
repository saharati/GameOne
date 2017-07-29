package configs;

import java.awt.Color;
import java.util.logging.Logger;

import util.parsers.PropertiesParser;

/**
 * Load all client configurations files.
 * @author Sahar
 */
public final class Config
{
	private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
	
	// --------------------------------------------------
	// Property File Definitions
	// --------------------------------------------------
	private static final String NETWORK_FILE = "./configs/Network.properties";
	private static final String CHAT_FILE = "./configs/Chat.properties";
	private static final String GAMES_FILE = "./configs/Games.properties";
	
	// --------------------------------------------------
	// Network Settings
	// --------------------------------------------------
	public static String SERVER_IP;
	public static int PORT;
	
	// --------------------------------------------------
	// Chat Settings
	// --------------------------------------------------
	public static boolean CHAT_SOUND;
	public static int CHAT_DIRECTION;
	public static boolean MOVE_CARET;
	
	// --------------------------------------------------
	// Games Settings
	// --------------------------------------------------
	public static boolean GAME_BEEP;
	
	// --------------------------------------------------
	// Hidden
	// --------------------------------------------------
	public static final Color UI_COLOR = new Color(0x7B, 0xD9, 0xF1);
	
	public static void load()
	{
		// Load Network.properties file (if exists)
		final PropertiesParser network = new PropertiesParser(NETWORK_FILE);
		
		SERVER_IP = network.getProperty("ServerIP", "127.0.0.1");
		PORT = network.getProperty("Port", 777);
		
		// Load Chat.properties file (if exists)
		final PropertiesParser chat = new PropertiesParser(CHAT_FILE);
		
		CHAT_SOUND = chat.getProperty("ChatSound", true);
		CHAT_DIRECTION = chat.getProperty("ChatDirection", 1);
		MOVE_CARET = chat.getProperty("MoveCaret", true);
		
		// Load Games.properties file (if exists)
		final PropertiesParser games = new PropertiesParser(GAMES_FILE);
		
		GAME_BEEP = games.getProperty("GameBeep", true);
		
		LOGGER.info("Config loaded!");
	}
}