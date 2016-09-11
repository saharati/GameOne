package handlers;

import server.objects.User;

/**
 * Interface for admin commands.
 * @author Sahar
 */
public interface IAdminCommandHandler
{
	public boolean useCommand(final String command, final User user);
	
	public String[] getVoicedCommandList();
}