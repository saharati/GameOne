package handlers.admin;

import java.util.StringTokenizer;

import data.sql.AnnouncementsTable;
import handlers.IAdminCommandHandler;
import server.objects.User;

public final class Announce implements IAdminCommandHandler
{
	private static final String[] COMMANDS = {"addAnnounce", "deleteAnnounce"};
	
	@Override
	public boolean useCommand(final String command, final User user)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "addAnnounce":
				if (st.countTokens() < 2)
				{
					user.sendPacket("Server", "Required syntax: addAnnounce <order> <msg>");
					return false;
				}
				
				final String orderString = st.nextToken();
				int order;
				try
				{
					order = Integer.parseInt(orderString);
				}
				catch (final NumberFormatException e)
				{
					user.sendPacket("Server", "Required syntax: addAnnounce <order> <msg>");
					return false;
				}
				
				final String msg = command.substring(command.indexOf(orderString) + 1).trim();
				if (msg.isEmpty())
				{
					user.sendPacket("Server", "Required syntax: addAnnounce <order> <msg>");
					return false;
				}
				
				AnnouncementsTable.getInstance().addAnnouncement(order, msg);
				break;
			case "deleteAnnounce":
				if (!st.hasMoreTokens())
				{
					user.sendPacket("Server", "Required syntax: deleteAnnounce <order>");
					return false;
				}
				
				try
				{
					order = Integer.parseInt(st.nextToken());
				}
				catch (final NumberFormatException e)
				{
					user.sendPacket("Server", "Required syntax: deleteAnnounce <order>");
					return false;
				}
				
				AnnouncementsTable.getInstance().deleteAnnouncement(order);
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