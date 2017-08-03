package handlers.admin;

import java.util.StringTokenizer;

import data.UsersTable;
import handlers.IAdminCommandHandler;
import network.response.LogoutResponse;
import server.objects.AccessLevel;
import server.objects.GameClient;
import server.objects.User;

public final class ManageAccess implements IAdminCommandHandler
{
	private static final String[] COMMANDS = {"kick", "ban", "unban", "setgm", "removegm"};
	
	@Override
	public void useCommand(final String command, final User user)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		if (!st.hasMoreTokens())
		{
			user.sendPacket("Server", "Required syntax: " + cmd + " <username>");
			return;
		}
		
		final String username = st.nextToken().trim();
		final User target = UsersTable.getInstance().getUserByName(username);
		if (target == null)
		{
			user.sendPacket("Server", "Username doesn't exist.");
			return;
		}
		if (target == user)
		{
			user.sendPacket("Server", "You cannot use this command on yourself.");
			return;
		}
		
		if (cmd.equalsIgnoreCase("kick"))
		{
			if (target.isOnline())
			{
				final GameClient client = target.getClient();
				client.setUser(null);
				client.sendPacket(LogoutResponse.STATIC_PACKET);
				
				user.sendPacket("Server", "User " + target.getUsername() + " was kicked.");
			}
			else
				user.sendPacket("Server", "This user is not online.");
		}
		else if (cmd.equalsIgnoreCase("ban"))
		{
			target.setAccessLevel(AccessLevel.BANNED);
			if (target.isOnline())
			{
				final GameClient client = target.getClient();
				client.setUser(null);
				client.sendPacket(LogoutResponse.STATIC_PACKET);
			}
			
			user.sendPacket("Server", "User " + target.getUsername() + " was banned.");
		}
		else if (cmd.equalsIgnoreCase("unban"))
		{
			target.setAccessLevel(AccessLevel.NORMAL);
			
			user.sendPacket("Server", "User " + target.getUsername() + " was unbanned.");
		}
		else if (cmd.equalsIgnoreCase("removegm"))
		{
			target.setAccessLevel(AccessLevel.NORMAL);
			
			user.sendPacket("Server", "Revoked GM status from " + target.getUsername() + ".");
		}
		else
		{
			target.setAccessLevel(AccessLevel.GM);
			
			user.sendPacket("Server", "User " + target.getUsername() + " was given GM status.");
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}