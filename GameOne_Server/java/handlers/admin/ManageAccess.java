package handlers.admin;

import java.util.StringTokenizer;

import data.sql.UsersTable;
import handlers.IAdminCommandHandler;
import server.network.outgoing.LogoutResponse;
import server.objects.AccessLevel;
import server.objects.User;

/**
 * Announce related admin commands.
 * @author Sahar
 */
public final class ManageAccess implements IAdminCommandHandler
{
	private static final String[] COMMANDS = {"kick", "ban", "unban", "setgm", "removegm"};
	
	@Override
	public boolean useCommand(final String command, final User user)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		if (!st.hasMoreTokens())
		{
			user.sendPacket("Server", "Required syntax: " + cmd + " <username>");
			return false;
		}
		final String username = st.nextToken().trim();
		final User target = UsersTable.getInstance().getUserByName(username);
		if (target == null)
		{
			user.sendPacket("Server", "Username doesn't exist.");
			return false;
		}
		if (target == user)
		{
			user.sendPacket("Server", "You cannot use this command on yourself.");
			return false;
		}
		
		switch (cmd)
		{
			case "kick":
				if (target.isOnline())
				{
					target.getClient().setUser(null);
					target.sendPacket(LogoutResponse.STATIC_PACKET);
					target.onLogout();
				}
				else
				{
					user.sendPacket("Server", "This user is not online.");
					return false;
				}
				break;
			case "ban":
				target.setAccessLevel(AccessLevel.BANNED);
				
				if (target.isOnline())
				{
					target.getClient().setUser(null);
					target.sendPacket(LogoutResponse.STATIC_PACKET);
					target.onLogout();
				}
				break;
			case "unban":
			case "removegm":
				target.setAccessLevel(AccessLevel.NORMAL);
				break;
			case "setgm":
				target.setAccessLevel(AccessLevel.GM);
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}