package handlers.admin;

import java.util.StringTokenizer;

import handlers.IAdminCommandHandler;
import server.Shutdown;
import server.objects.User;

/**
 * Shutdown related admin commands.
 * @author Sahar
 */
public final class AdminShutdown implements IAdminCommandHandler
{
	private static final String[] COMMANDS = {"shutdown", "restart", "abort"};
	
	@Override
	public boolean useCommand(final String command, final User user)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "shutdown":
			case "restart":
				if (!st.hasMoreTokens())
				{
					user.getClient().sendPacket("Server", "Required syntax: " + cmd + " <time>");
					return false;
				}
				
				int time;
				try
				{
					time = Integer.parseInt(st.nextToken());
				}
				catch (final NumberFormatException e)
				{
					user.getClient().sendPacket("Server", "Required syntax: " + cmd + " <time>");
					return false;
				}
				
				Shutdown.getInstance().startShutdown(user.getName(), time, cmd.equalsIgnoreCase("restart"));
				break;
			case "abort":
				Shutdown.getInstance().abort(user.getName());
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