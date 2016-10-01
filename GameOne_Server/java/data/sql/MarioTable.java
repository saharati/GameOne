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
import objects.mario.MarioType;
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
	
	protected MarioTable()
	{
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(SELECT);
			final ResultSet rs = ps.executeQuery())
		{
			final MarioType[] values = MarioType.values();
			while (rs.next())
				_objects.add(new MarioObject(rs.getInt("x"), rs.getInt("y"), values[rs.getInt("type")]));
			
			LOGGER.info("Loaded " + _objects.size() + " mario objects from database.");
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed loading MarioTable: ", e);
		}
	}

	public boolean updateDatabase(final List<MarioObject> objects)
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
					ps.setInt(3, o.getType().ordinal());
					ps.addBatch();
				}
				ps.executeBatch();
			}
			
			_objects.clear();
			_objects.addAll(objects);
			return true;
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed updating MarioTable: ", e);
			return false;
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
		protected static final MarioTable INSTANCE = new MarioTable();
	}
}