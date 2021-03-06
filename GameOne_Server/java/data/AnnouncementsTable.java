package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.objects.GameClient;
import util.database.AccessDatabase;
import util.database.Database;

public final class AnnouncementsTable
{
	private static final Logger LOGGER = Logger.getLogger(AnnouncementsTable.class.getName());
	private static final String SELECT_ANNOUNCEMENTS = "SELECT * FROM `announcements`";
	private static final String DELETE_ANNOUNCEMENT = "DELETE FROM `announcements` WHERE `order`=?";
	private static final String ADD_ANNOUNCEMENT = "INSERT INTO `announcements` VALUES (?, ?)";
	private static final String SYNC_DELETE_ANNOUNCEMENTS = "DELETE FROM `announcements`";
	
	private final Map<Integer, String> _announcements = new ConcurrentSkipListMap<>();
	
	protected AnnouncementsTable()
	{
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(SELECT_ANNOUNCEMENTS);
			final ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
				_announcements.put(rs.getInt("order"), rs.getString("msg"));
			
			LOGGER.info("Loaded " + _announcements.size() + " announcements from database.");
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed loading announcements: ", e);
		}
	}
	
	public void sync()
	{
		try (final Connection con = AccessDatabase.getConnection())
		{
			try (final PreparedStatement ps = con.prepareStatement(SYNC_DELETE_ANNOUNCEMENTS))
			{
				ps.execute();
			}
			if (!_announcements.isEmpty())
			{
				try (final PreparedStatement ps = con.prepareStatement(ADD_ANNOUNCEMENT))
				{
					for (final Entry<Integer, String> ann : _announcements.entrySet())
					{
						ps.setInt(1, ann.getKey());
						ps.setString(2, ann.getValue());
						ps.addBatch();
					}
					
					ps.executeBatch();
				}
			}
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed syncing AnnouncementsTable: ", e);
		}
	}
	
	public void showAnnouncements(final GameClient client)
	{
		_announcements.values().forEach(msg -> client.sendPacket("Server", msg));
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
			
			_announcements.put(order, text);
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed adding announcement: ", e);
		}
	}
	
	public void deleteAnnouncement(final int order)
	{
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
		protected static final AnnouncementsTable INSTANCE = new AnnouncementsTable();
	}
}