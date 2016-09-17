package util;

import data.sql.UsersTable;
import network.response.MessageResponse;
import server.objects.User;

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
	
	public static void toAllUsersExcept(final String text, final User user)
	{
		final MessageResponse msg = new MessageResponse(text);
		
		UsersTable.getInstance().getOnlineUsers().filter(u -> u != user).forEach(u -> u.sendPacket(msg));
	}
}