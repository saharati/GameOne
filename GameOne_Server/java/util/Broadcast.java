package util;

import data.UsersTable;
import network.PacketWriter;
import network.response.MessageResponse;
import objects.GameId;
import server.objects.GameClient;

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
	
	public static void toAllUsersOfGame(final PacketWriter packet, final GameId gameId)
	{
		UsersTable.getInstance().getOnlineUsers().filter(u -> u.getCurrentGame() == gameId).forEach(u -> u.sendPacket(packet));
	}
}