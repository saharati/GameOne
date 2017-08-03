package handlers.admin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import handlers.IAdminCommandHandler;
import server.objects.User;
import util.configs.CommonConfig;
import util.configs.Config;
import util.configs.GameConfig;

public final class Reload implements IAdminCommandHandler
{
	private static final Logger LOGGER = Logger.getLogger(Reload.class.getName());
	private static final String[] COMMANDS = {"reload"};
	
	@Override
	public void useCommand(final String command, final User user)
	{
		try
		{
			CommonConfig.load();
		}
		catch (final SecurityException | URISyntaxException | IOException e)
		{
			LOGGER.log(Level.WARNING, "Failed reloading CommonConfig: ", e);
		}
		
		GameConfig.load();
		Config.load();
		
		user.sendPacket("Server", "Configs reloaded successfully.");
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}