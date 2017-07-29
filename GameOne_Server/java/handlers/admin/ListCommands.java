package handlers.admin;

import handlers.AdminCommandHandler;
import handlers.IAdminCommandHandler;
import server.objects.User;

public final class ListCommands implements IAdminCommandHandler
{
	private static final String[] COMMANDS = {"list"};
	
	@Override
	public boolean useCommand(final String command, final User user)
	{
		AdminCommandHandler.getInstance().getAllAvailableCommands().forEach(cmd -> user.sendPacket("Server", cmd));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}