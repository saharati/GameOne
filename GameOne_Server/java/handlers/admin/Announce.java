package handlers.admin;

import java.util.StringTokenizer;

import data.AnnouncementsTable;
import handlers.IAdminCommandHandler;
import server.objects.User;

public final class Announce implements IAdminCommandHandler
{
	private static final String[] COMMANDS = {"addAnnounce", "deleteAnnounce"};
	
	@Override
	public void useCommand(final String command, final User user)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		if (cmd.equalsIgnoreCase("addAnnounce"))
		{
			if (st.countTokens() < 2)
			{
				user.sendPacket("Server", "Required syntax: addAnnounce <order> <msg>");
				return;
			}
			
			final String orderString = st.nextToken();
			final int order;
			try
			{
				order = Integer.parseInt(orderString);
			}
			catch (final NumberFormatException e)
			{
				user.sendPacket("Server", "Required syntax: addAnnounce <order> <msg>");
				return;
			}
			
			final String msg = command.substring(command.indexOf(orderString) + 1).trim();
			if (msg.isEmpty())
			{
				user.sendPacket("Server", "Required syntax: addAnnounce <order> <msg>");
				return;
			}
			
			AnnouncementsTable.getInstance().addAnnouncement(order, msg);
			user.sendPacket("Server", "Announcement added successfully.");
		}
		else
		{
			if (!st.hasMoreTokens())
			{
				user.sendPacket("Server", "Required syntax: deleteAnnounce <order>");
				return;
			}
			
			final int order;
			try
			{
				order = Integer.parseInt(st.nextToken());
			}
			catch (final NumberFormatException e)
			{
				user.sendPacket("Server", "Required syntax: deleteAnnounce <order>");
				return;
			}
			
			AnnouncementsTable.getInstance().deleteAnnouncement(order);
			user.sendPacket("Server", "Announcement deleted successfully.");
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}