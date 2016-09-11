package handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import handlers.admin.*;

/**
 * Class containing admin commands.
 * @author Sahar
 */
public final class AdminCommandHandler
{
	private final Map<String, IAdminCommandHandler> _datatable = new HashMap<>();
	
	private AdminCommandHandler()
	{
		registerHandler(new AdminShutdown());
		registerHandler(new Announce());
		registerHandler(new ListCommands());
	}
	
	public void registerHandler(final IAdminCommandHandler handler)
	{
		for (final String id : handler.getVoicedCommandList())
			_datatable.put(id, handler);
	}
	
	public IAdminCommandHandler getHandler(final String voicedCommand)
	{
		return _datatable.get(voicedCommand);
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	public List<String> getAllAvailableCommands()
	{
		final List<String> list = new ArrayList<>();
		for (final IAdminCommandHandler a : _datatable.values())
			for (final String c : a.getVoicedCommandList())
				list.add(c);
		
		return list;
	}
	
	public static AdminCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final AdminCommandHandler INSTANCE = new AdminCommandHandler();
	}
}