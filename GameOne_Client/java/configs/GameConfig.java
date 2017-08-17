package configs;

import java.util.Map;
import java.util.logging.Logger;

public final class GameConfig
{
	private static final Logger LOGGER = Logger.getLogger(GameConfig.class.getName());
	
	// --------------------------------------------------
	// Checkers Settings
	// --------------------------------------------------
	public static boolean QUEEN_SINGLE_STEP;
	public static boolean BURN_PLAYERS;
	public static boolean CHECKERS_PAINT_MOVES;
	public static boolean CHECKERS_PAINT_ROUTE;
	
	// --------------------------------------------------
	// Chess Settings
	// --------------------------------------------------
	public static boolean CHESS_PAINT_MOVES;
	public static boolean CHESS_PAINT_ROUTE;
	public static boolean CHOOSE_ON_PROMOTE;
	
	public static void load(final Map<String, String> configs)
	{
		// Checkers
		QUEEN_SINGLE_STEP = Boolean.parseBoolean(configs.get("QueenSingleStep"));
		BURN_PLAYERS = Boolean.parseBoolean(configs.get("BurnPlayers"));
		CHECKERS_PAINT_MOVES = Boolean.parseBoolean(configs.get("CheckersPaintMoves"));
		CHECKERS_PAINT_ROUTE = Boolean.parseBoolean(configs.get("CheckersPaintRoute"));
		
		// Chess
		CHESS_PAINT_MOVES = Boolean.parseBoolean(configs.get("ChessPaintMoves"));
		CHESS_PAINT_ROUTE = Boolean.parseBoolean(configs.get("ChessPaintRoute"));
		CHOOSE_ON_PROMOTE = Boolean.parseBoolean(configs.get("ChooseOnPromote"));
		
		LOGGER.info("GameConfig loaded!");
	}
}