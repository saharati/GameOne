package util.configs;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import util.parsers.PropertiesParser;

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
		CONFIGS.put("QueenSingleStep", String.valueOf(games.getProperty("QueenSingleStep", false)));
		CONFIGS.put("BurnPlayers", String.valueOf(games.getProperty("BurnPlayers", true)));
		CONFIGS.put("CheckersPaintMoves", String.valueOf(games.getProperty("CheckersPaintMoves", true)));
		CONFIGS.put("CheckersPaintRoute", String.valueOf(games.getProperty("CheckersPaintRoute", true)));
		
		// Chess
		CONFIGS.put("ChessPaintMoves", String.valueOf(games.getProperty("ChessPaintMoves", true)));
		CONFIGS.put("ChessPaintRoute", String.valueOf(games.getProperty("ChessPaintRoute", true)));
		CONFIGS.put("ChooseOnPromote", String.valueOf(games.getProperty("ChooseOnPromote", true)));
		
		LOGGER.info("GameConfig loaded!");
	}
}