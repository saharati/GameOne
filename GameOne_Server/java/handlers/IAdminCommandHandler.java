package handlers;

import server.objects.User;

public interface IAdminCommandHandler
{
	public void useCommand(final String command, final User user);
	
	public String[] getVoicedCommandList();
}