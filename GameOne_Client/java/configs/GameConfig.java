package configs;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Class holding all game configs as they were received from the server.
 * @author Sahar
 */
public final class GameConfig
{
	private static final Logger LOGGER = Logger.getLogger(GameConfig.class.getName());
	
	// --------------------------------------------------
	// Checkers Settings
	// --------------------------------------------------
	public static boolean QUEEN_SINGLE_STEP;
	public static boolean BURN_PLAYERS;
	
	public static void load(final Map<String, String> configs)
	{
		QUEEN_SINGLE_STEP = Boolean.parseBoolean(configs.get("QueenSingleStep"));
		BURN_PLAYERS = Boolean.parseBoolean(configs.get("BurnPlayers"));
		
		LOGGER.info("GameConfig loaded!");
	}
}