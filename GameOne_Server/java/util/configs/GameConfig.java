package util.configs;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import util.parsers.PropertiesParser;

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
		final PropertiesParser games = new PropertiesParser(GAMES_FILE);
		
		// Checkers
		CONFIGS.put("QueenSingleStep", games.getProperty("QueenSingleStep", "False"));
		CONFIGS.put("BurnPlayers", games.getProperty("BurnPlayers", "True"));
		CONFIGS.put("CheckersPaintMoves", games.getProperty("CheckersPaintMoves", "True"));
		CONFIGS.put("CheckersPaintRoute", games.getProperty("CheckersPaintRoute", "True"));
		
		// Chess
		CONFIGS.put("ChessPaintMoves", games.getProperty("ChessPaintMoves", "True"));
		CONFIGS.put("ChessPaintRoute", games.getProperty("ChessPaintRoute", "True"));
		CONFIGS.put("ChooseOnPromote", games.getProperty("ChooseOnPromote", "True"));
		
		LOGGER.info("GameConfig loaded!");
	}
}