package data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import objects.mario.MarioObject;
import util.database.Database;

/**
 * Mario game objects.
 * @author Sahar
 */
public final class MarioTable
{
	private static final Logger LOGGER = Logger.getLogger(MarioTable.class.getName());
	
	private static final String SELECT = "SELECT * FROM mario";
	private static final String CLEAR = "DELETE FROM mario";
	private static final String INSERT = "INSERT INTO mario VALUES (?, ?, ?)";
	
	private final List<MarioObject> _objects = new CopyOnWriteArrayList<>();
	
	private MarioTable()
	{
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(SELECT);
			final ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
				_objects.add(new MarioObject(rs.getInt("x"), rs.getInt("y"), rs.getString("type")));
			
			LOGGER.info("Loaded " + _objects.size() + " mario objects from database.");
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed loading MarioTable: ", e);
		}
	}

	public void updateObjects(final List<MarioObject> objects)
	{
		try (final Connection con = Database.getConnection())
		{
			try (final PreparedStatement ps = con.prepareStatement(CLEAR))
			{
				ps.execute();
			}
			try (final PreparedStatement ps = con.prepareStatement(INSERT))
			{
				for (final MarioObject o : objects)
				{
					ps.setInt(1, o.getX());
					ps.setInt(2, o.getY());
					ps.setString(3, o.getType());
					ps.addBatch();
				}
				ps.executeBatch();
			}
			
			_objects.clear();
			_objects.addAll(objects);
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed updating MarioTable: ", e);
		}
	}
	
	public List<MarioObject> getObjects()
	{
		return _objects;
	}
	
	public static final MarioTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final MarioTable INSTANCE = new MarioTable();
	}
}