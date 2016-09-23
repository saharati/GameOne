package util;

import data.sql.UsersTable;
import network.PacketWriter;
import network.response.MessageResponse;
import server.objects.GameClient;

/**
 * Class responsible to broadcasting packets depending on situation.
 * @author Sahar
 */
public final class Broadcast
{
	public static void toAllUsers(final PacketWriter packet)
	{
		UsersTable.getInstance().getOnlineUsers().forEach(u -> u.sendPacket(packet));
	}
	
	public static void toAllUsers(final String text)
	{
		toAllUsers(new MessageResponse(text));
	}
	
	public static void toAllExcept(final String text, final GameClient client)
	{
		final MessageResponse msg = new MessageResponse(text);
		
		UsersTable.getInstance().getOnlineUsers().filter(u -> u.getClient() != client).forEach(u -> u.sendPacket(msg));
	}
}