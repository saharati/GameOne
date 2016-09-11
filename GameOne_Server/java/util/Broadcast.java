package util;

import java.util.ArrayList;
import java.util.List;

import server.network.outgoing.MessageResponse;
import server.objects.GameClient;

/**
 * Class responsible to broadcasting packets depending on situation.
 * @author Sahar
 */
public final class Broadcast
{
	public static final List<GameClient> THREADS = new ArrayList<>();
	
	public static void toAllUsers(final String text)
	{
		final MessageResponse msg = new MessageResponse(text);
		
		THREADS.stream().filter(t -> t.isAuthed()).forEach(t -> t.sendPacket(msg));
	}
	
	public static void toAllUsersExcept(final String text, final GameClient client)
	{
		final MessageResponse msg = new MessageResponse(text);
		
		THREADS.stream().filter(t -> t.isAuthed() && t != client).forEach(t -> t.sendPacket(msg));
	}
}