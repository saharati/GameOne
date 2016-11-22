package data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import network.response.LoginResponse;
import objects.GameId;
import server.objects.AccessLevel;
import server.objects.GameStat;
import server.objects.User;
import util.configs.Config;
import util.database.AccessDatabase;
import util.database.Database;

/**
 * Class holding all registered users.
 * @author Sahar
 */
public final class UsersTable
{
	private static final Logger LOGGER = Logger.getLogger(UsersTable.class.getName());
	
	private static final String SELECT_USER = "SELECT * FROM users";
	private static final String SELECT_STATS = "SELECT gameId, score, wins, loses FROM user_games WHERE userId=?";
	private static final String INSERT_USER = "INSERT INTO users (username, password, lastIp, lastMac, accessLevel) VALUES (?, ?, ?, ?, ?)";
	private static final String SYNC_DELETE_USERS = "DELETE FROM users";
	private static final String SYNC_DELETE_USER_GAMES = "DELETE FROM user_games";
	private static final String SYNC_ADD_USER = "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SYNC_ADD_USER_GAME = "INSERT INTO user_games VALUES (?, ?, ?, ?, ?)";
	
	private final Map<Integer, User> _users = new HashMap<>();
	
	protected UsersTable()
	{
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement(SELECT_USER);
			final ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final Map<GameId, GameStat> gameStats = new HashMap<>();
				try (final PreparedStatement ps2 = con.prepareStatement(SELECT_STATS))
				{
					ps2.setInt(1, rs.getInt("id"));
					try (final ResultSet rs2 = ps2.executeQuery())
					{
						while (rs2.next())
						{
							final GameId gameId = GameId.values()[rs2.getInt("gameId")];
							gameStats.put(gameId, new GameStat(gameId, rs2.getInt("score"), rs2.getInt("wins"), rs2.getInt("loses")));
						}
					}
				}
				
				_users.put(rs.getInt("id"), new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("lastIp"), rs.getString("lastMac"), AccessLevel.values()[rs.getInt("accessLevel")], gameStats));
			}
			
			LOGGER.info("Loaded " + _users.size() + " users from database.");
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed loading UsersTable: ", e);
		}
	}
	
	public void sync()
	{
		try (final Connection con = AccessDatabase.getConnection())
		{
			try (final PreparedStatement ps = con.prepareStatement(SYNC_DELETE_USERS);
				final PreparedStatement ps2 = con.prepareStatement(SYNC_DELETE_USER_GAMES))
			{
				ps.execute();
				ps2.execute();
			}
			try (final PreparedStatement ps = con.prepareStatement(SYNC_ADD_USER);
				final PreparedStatement ps2 = con.prepareStatement(SYNC_ADD_USER_GAME))
			{
				for (final User user : _users.values())
				{
					ps.setInt(1, user.getId());
					ps.setString(2, user.getUsername());
					ps.setString(3, user.getPassword());
					ps.setString(4, user.getIp());
					ps.setString(5, user.getMac());
					ps.setInt(6, user.getAccessLevel().ordinal());
					ps.addBatch();
					
					ps2.setInt(1, user.getId());
					for (final GameStat gameStat : user.getGameStats().values())
					{
						ps2.setInt(2, gameStat.getGameId().ordinal());
						ps2.setInt(3, gameStat.getScore());
						ps2.setInt(4, gameStat.getWins());
						ps2.setInt(5, gameStat.getLoses());
						ps2.addBatch();
					}
				}
				
				ps.executeBatch();
				ps2.executeBatch();
			}
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed syncing UsersTable: ", e);
		}
	}
	
	public Map<Integer, User> getUsers()
	{
		return _users;
	}
	
	public Stream<User> getOnlineUsers()
	{
		return _users.values().stream().filter(u -> u.isOnline());
	}
	
	public long getOnlineCount()
	{
		return getOnlineUsers().count();
	}
	
	public User getUserByName(final String name)
	{
		return _users.values().stream().filter(u -> u.getUsername().equals(name)).findAny().orElse(null);
	}
	
	public LoginResponse tryToLogin(final String username, final String password, final String ip, final String mac)
	{
		if (getOnlineCount() > Config.MAXIMUM_ONLINE_USERS)
			return LoginResponse.SERVER_FULL;
		
		final User user = getUserByName(username);
		if (user == null)
		{
			if (Config.AUTO_CREATE_ACCOUNTS)
			{
				try (final Connection con = Database.getConnection();
					final PreparedStatement ps = con.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS))
				{
					final AccessLevel access = _users.isEmpty() ? AccessLevel.GM : AccessLevel.NORMAL;
					
					ps.setString(1, username);
					ps.setString(2, password);
					ps.setString(3, ip);
					ps.setString(4, mac);
					ps.setInt(5, access.ordinal());
					ps.executeUpdate();
					
					try (final ResultSet rs = ps.getGeneratedKeys())
					{
						if (rs.next())
						{
							final User newUser = new User(rs.getInt(1), username, password, ip, mac, access, new HashMap<>());
							_users.put(newUser.getId(), newUser);
							
							return LoginResponse.LOGIN_OK;
						}
						
						return LoginResponse.SERVER_ERROR;
					}
				}
				catch (final SQLException e)
				{
					LOGGER.log(Level.WARNING, "Failed reading login packet: ", e);
					
					return LoginResponse.SERVER_ERROR;
				}
			}
			
			return LoginResponse.LOGIN_FAILED;
		}
		
		if (!user.getPassword().equals(password))
			return LoginResponse.LOGIN_FAILED;
		if (user.getAccessLevel() == AccessLevel.BANNED)
			return LoginResponse.USER_BANNED;
		if (user.isOnline())
			return LoginResponse.ALREADY_ONLINE;
		
		return LoginResponse.LOGIN_OK;
	}
	
	public String[][] getTop5(final GameId gameId)
	{
		final String[][] ret = new String[5][4];
		for (final String[] r : ret)
			Arrays.fill(r, "");
		
		final List<User> users = new CopyOnWriteArrayList<>(_users.values());
		User max = null;
		for (int i = 0;i < 5 && !users.isEmpty();i++)
		{
			for (final User user : users)
			{
				if (user.hasGame(gameId))
				{
					if (max == null)
						max = user;
					else if (max.getGame(gameId).getWins() < user.getGame(gameId).getWins())
						max = user;
					else if (max.getGame(gameId).getScore() < user.getGame(gameId).getScore())
						max = user;
					else if (max.getGame(gameId).getLoses() > user.getGame(gameId).getLoses())
						max = user;
				}
				else
					users.remove(user);
			}
			if (max == null)
				break;
			
			users.remove(max);
			
			ret[i][0] = max.getUsername();
			ret[i][1] = String.valueOf(max.getGame(gameId).getWins());
			ret[i][2] = String.valueOf(max.getGame(gameId).getScore());
			ret[i][3] = String.valueOf(max.getGame(gameId).getLoses());
			
			max = null;
		}
		
		return ret;
	}
	
	public static final UsersTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final UsersTable INSTANCE = new UsersTable();
	}
}