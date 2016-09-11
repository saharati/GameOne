package data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.network.outgoing.MessageResponse;
import server.objects.GameClient;
import util.StringUtil;
import util.database.Database;

/**
 * Loads announcements from database.
 * @author Sahar
 */
public final class AnnouncementsTable
{
	private static final Logger LOGGER = Logger.getLogger(AnnouncementsTable.class.getName());
	
	private static final String SELECT_ANNOUNCEMENTS = "SELECT * FROM announcements";
	private static final String DELETE_ANNOUNCEMENT = "DELETE FROM announcements WHERE order=?";
	private static final String ADD_ANNOUNCEMENT = "INSERT INTO announcements (?, ?)";
	
	private final Map<Integer, MessageResponse> _announcements = new ConcurrentSkipListMap<>();
	
	private AnnouncementsTable()
	{
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(SELECT_ANNOUNCEMENTS);
			final ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final String msg = StringUtil.refineBeforeSend("Server", rs.getString("msg"));
				final MessageResponse packet = new MessageResponse(msg);
				
				_announcements.put(rs.getInt("order"), packet);
			}
			
			LOGGER.info("Loaded " + _announcements.size() + " announcements from database.");
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed loading announcements: ", e);
		}
	}
	
	public void showAnnouncements(final GameClient client)
	{
		_announcements.values().forEach(a -> client.sendPacket(a));
	}
	
	public void addAnnouncement(final int order, final String text)
	{
		deleteAnnouncement(order);
		
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(ADD_ANNOUNCEMENT))
		{
			ps.setInt(1, order);
			ps.setString(2, text);
			ps.execute();
			
			final String msg = StringUtil.refineBeforeSend("Server", text);
			final MessageResponse packet = new MessageResponse(msg);
			
			_announcements.put(order, packet);
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed adding announcement: ", e);
		}
	}
	
	public void deleteAnnouncement(final int order)
	{
		if (!_announcements.containsKey(order))
			return;
		
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(DELETE_ANNOUNCEMENT))
		{
			ps.setInt(1, order);
			ps.execute();
			
			_announcements.remove(order);
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed deleting announcement: ", e);
		}
	}
	
	public static AnnouncementsTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final AnnouncementsTable INSTANCE = new AnnouncementsTable();
	}
}