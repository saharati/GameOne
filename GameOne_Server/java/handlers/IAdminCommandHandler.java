package handlers;

import server.objects.User;

public interface IAdminCommandHandler
{
	public boolean useCommand(final String command, final User user);
	
	public String[] getVoicedCommandList();
}