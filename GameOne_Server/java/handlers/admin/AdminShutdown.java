package handlers.admin;

import java.util.StringTokenizer;

import handlers.IAdminCommandHandler;
import server.Shutdown;
import server.objects.User;

public final class AdminShutdown implements IAdminCommandHandler
{
	private static final String[] COMMANDS = {"shutdown", "restart", "abort"};
	
	@Override
	public void useCommand(final String command, final User user)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		if (cmd.equalsIgnoreCase("shutdown") || cmd.equalsIgnoreCase("restart"))
		{
			if (!st.hasMoreTokens())
			{
				user.sendPacket("Server", "Required syntax: " + cmd + " <time>");
				return;
			}
			
			final int time;
			try
			{
				time = Integer.parseInt(st.nextToken());
			}
			catch (final NumberFormatException e)
			{
				user.sendPacket("Server", "Required syntax: " + cmd + " <time>");
				return;
			}
			
			Shutdown.getInstance().startShutdown(user.getUsername(), time, cmd.equalsIgnoreCase("restart"));
		}
		else
			Shutdown.getInstance().abort(user.getUsername());
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}