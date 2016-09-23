package data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import objects.pacman.PacmanObject;
import util.database.Database;

/**
 * Pacman game objects.
 * @author Sahar
 */
public final class PacmanTable
{
	private static final Logger LOGGER = Logger.getLogger(PacmanTable.class.getName());
	
	private static final String SELECT_MAPS = "SELECT DISTINCT mapId FROM pacman";
	private static final String SELECT_MAP = "SELECT i, j, type FROM pacman WHERE mapId=?";
	private static final String INSERT_OBJECT = "INSERT INTO pacman VALUES (?, ?, ?, ?)";
	private static final String DELETE_MAP = "DELETE FROM pacman WHERE mapId=?";
	
	private final Map<Integer, PacmanObject[][]> _maps = new ConcurrentHashMap<>();
	
	private PacmanTable()
	{
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(SELECT_MAPS);
			final ResultSet rs = ps.executeQuery())
		{
			final PacmanObject[] pacmanValues = PacmanObject.values();
			while (rs.next())
			{
				final int mapId = rs.getInt("mapId");
				final PacmanObject[][] objects = new PacmanObject[16][12];
				try (final PreparedStatement ps2 = con.prepareStatement(SELECT_MAP))
				{
					ps2.setInt(1, mapId);
					try (final ResultSet rs2 = ps2.executeQuery())
					{
						while (rs2.next())
							objects[rs2.getInt("i")][rs2.getInt("j")] = pacmanValues[rs2.getInt("type")];
						
						_maps.put(mapId, objects);
					}
				}
			}
			
			LOGGER.info("Loaded " + _maps.size() + " pacman maps from database.");
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed loading PacmanTable: ", e);
		}
	}
	
	public void addMap(final int id, final PacmanObject[][] objects)
	{
		int newId = 0;
		if (id == -1)
		{
			for (final int ids : _maps.keySet())
				if (newId < ids)
					newId = ids;
			
			newId++;
		}
		else
			newId = id;
		
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(INSERT_OBJECT))
		{
			ps.setInt(1, id);
			for (int i = 0;i < objects.length;i++)
			{
				for (int j = 0;j < objects[i].length;j++)
				{
					ps.setInt(2, i);
					ps.setInt(3, j);
					ps.setInt(4, objects[i][j].ordinal());
					ps.addBatch();
				}
			}
			ps.executeBatch();
			
			_maps.put(id, objects);
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed adding pacman map: ", e);
		}
	}
	
	public void removeMap(final int id)
	{
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(DELETE_MAP))
		{
			ps.setInt(1, id);
			ps.execute();

			_maps.remove(id);
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed deleting pacman map: ", e);
		}
	}
	
	public void updateMap(final int id, final PacmanObject[][] objects)
	{
		removeMap(id);
		addMap(id, objects);
	}
	
	public Map<Integer, PacmanObject[][]> getMaps()
	{
		return _maps;
	}
	
	public static final PacmanTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final PacmanTable INSTANCE = new PacmanTable();
	}
}