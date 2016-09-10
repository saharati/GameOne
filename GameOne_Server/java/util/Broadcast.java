package util;

import java.util.ArrayList;
import java.util.List;

import server.objects.GameClient;

/**
 * Class responsible to broadcasting packets depending on situation.
 * @author Sahar
 */
public class Broadcast
{
	public static final List<GameClient> THREADS = new ArrayList<>();
	
	public static void announceToOnlinePlayers(final String text)
	{
		// TODO sends a msg in chat window to all online players.
	}
}