package util;

import data.sql.UsersTable;
import network.response.MessageResponse;
import server.objects.GameClient;

/**
 * Class responsible to broadcasting packets depending on situation.
 * @author Sahar
 */
public final class Broadcast
{
	public static void toAllUsers(final String text)
	{
		final MessageResponse msg = new MessageResponse(text);
		
		UsersTable.getInstance().getOnlineUsers().forEach(u -> u.sendPacket(msg));
	}
	
	public static void toAllExcept(final String text, final GameClient client)
	{
		final MessageResponse msg = new MessageResponse(text);
		
		UsersTable.getInstance().getOnlineUsers().filter(u -> u.getClient() != client).forEach(u -> u.sendPacket(msg));
	}
}