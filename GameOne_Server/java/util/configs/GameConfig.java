package util.configs;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import util.parsers.properties.ExProperties;

/**
 * Load all game configurations files to be sent to the client.
 * @author Sahar
 */
public final class GameConfig
{
	private static final Logger LOGGER = Logger.getLogger(GameConfig.class.getName());
	
	// --------------------------------------------------
	// Property File Definitions
	// --------------------------------------------------
	private static final String GAMES_FILE = "./configs/Games.properties";
	
	// --------------------------------------------------
	// Games Settings
	// --------------------------------------------------
	public static Map<String, String> CONFIGS = new HashMap<>();
	
	public static void load()
	{
		CONFIGS.clear();
		
		// Load Games.properties file (if exists)
		final ExProperties games = new ExProperties(GAMES_FILE);
		
		CONFIGS.put("QueenSingleStep", games.getProperty("QueenSingleStep", "False"));
		CONFIGS.put("BurnPlayers", games.getProperty("BurnPlayers", "True"));
		
		LOGGER.info("GameConfig loaded!");
	}
}