package util;

import java.util.ArrayList;
import java.util.List;

import server.network.GameClient;

public class Broadcast
{
	public static final List<GameClient> THREADS = new ArrayList<>();
	
	public static void announceToOnlinePlayers(String text)
	{
		// TODO sends a msg in chat window to all online players.
	}
}