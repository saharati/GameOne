package handlers.admin;

import java.util.Set;

import handlers.AdminCommandHandler;
import handlers.IAdminCommandHandler;
import network.response.MessageResponse;
import server.objects.User;
import util.StringUtil;

/**
 * List available admin commands.
 * @author Sahar
 */
public final class ListCommands implements IAdminCommandHandler
{
	private static final String[] COMMANDS = {"list"};
	
	@Override
	public boolean useCommand(final String command, final User user)
	{
		final StringBuilder sb = new StringBuilder();
		final Set<String> availableCommands = AdminCommandHandler.getInstance().getAllAvailableCommands();
		availableCommands.forEach(c -> sb.append(StringUtil.refineBeforeSend("Server", c)));
		
		final MessageResponse msg = new MessageResponse(sb.toString());
		user.getClient().sendPacket(msg);
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}