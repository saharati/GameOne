package handlers;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import handlers.admin.*;

public final class AdminCommandHandler
{
	private final Map<String, IAdminCommandHandler> _datatable = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	protected AdminCommandHandler()
	{
		registerHandler(new AdminShutdown());
		registerHandler(new Announce());
		registerHandler(new ListCommands());
		registerHandler(new ManageAccess());
		registerHandler(new Reload());
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
	
	public Set<String> getAllAvailableCommands()
	{
		return _datatable.keySet();
	}
	
	public static AdminCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AdminCommandHandler INSTANCE = new AdminCommandHandler();
	}
}