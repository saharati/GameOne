package server.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.sql.AnnouncementsTable;
import network.PacketWriter;
import objects.GameId;
import util.Broadcast;
import util.StringUtil;
import util.database.Database;

/**
 * Class representing a user associated to GameClient.
 * @author Sahar
 */
public final class User
{
	private static final Logger LOGGER = Logger.getLogger(User.class.getName());
	
	private final int _id;
	private final String _username;
	private final String _password;
	private final Map<GameId, GameStat> _gameStats;
	
	private boolean _isOnline;
	private AccessLevel _accessLevel;
	private GameClient _client;
	private String _ip;
	private String _mac;
	private GameId _currentGame;
	
	public User(final int id, final String username, final String password, final String ip, final String mac, final AccessLevel accessLevel, final Map<GameId, GameStat> gameStats)
	{
		_id = id;
		_username = username;
		_password = password;
		_ip = ip;
		_mac = mac;
		_accessLevel = accessLevel;
		_gameStats = gameStats;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public String getUsername()
	{
		return _username;
	}
	
	public String getPassword()
	{
		return _password;
	}
	
	public boolean hasGame(final GameId gameId)
	{
		return _gameStats.containsKey(gameId);
	}
	
	public GameStat getGame(final GameId gameId)
	{
		return _gameStats.get(gameId);
	}
	
	public AccessLevel getAccessLevel()
	{
		return _accessLevel;
	}
	
	public void setAccessLevel(final AccessLevel accessLevel)
	{
		_accessLevel = accessLevel;
		
		try (final Connection con = Database.getConnection();
			final PreparedStatement ps = con.prepareStatement("UPDATE users SET accessLevel=? WHERE id=?"))
		{
			ps.setInt(1, _accessLevel.ordinal());
			ps.setInt(2, _id);
			ps.execute();
		}
		catch (final SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed updating accessLevel: ", e);
		}
	}
	
	public boolean isOnline()
	{
		return _isOnline;
	}
	
	public GameClient getClient()
	{
		return _client;
	}
	
	public String getIp()
	{
		return _ip;
	}
	
	public String getMac()
	{
		return _mac;
	}
	
	public GameId getCurrentGame()
	{
		return _currentGame;
	}
	
	public void setCurrentGame(final GameId currentGame)
	{
		_currentGame = currentGame;
	}
	
	public void onLogin(final GameClient client, final String ip, final String mac)
	{
		_client = client;
		_isOnline = true;
		
		if (!_ip.equals(ip) || !_mac.equals(mac))
		{
			_ip = ip;
			_mac = mac;
			
			try (final Connection con = Database.getConnection();
				final PreparedStatement ps = con.prepareStatement("UPDATE users SET lastIp=?, lastMac=? WHERE id=?"))
			{
				ps.setString(1, _ip);
				ps.setString(2, _mac);
				ps.setInt(3, _id);
				ps.execute();
			}
			catch (final SQLException e)
			{
				LOGGER.log(Level.WARNING, "Failed updating last ip/mac: ", e);
			}
		}
		
		final String logonMsg = StringUtil.refineBeforeSend("Server", _username + " has logged on.");
		Broadcast.toAllUsersExcept(logonMsg, this);
		
		AnnouncementsTable.getInstance().showAnnouncements(_client);
		
		if (_accessLevel == AccessLevel.GM)
		{
			sendPacket("Server", "You have admin priviliges.");
			sendPacket("Server", "Type //list for available commands.");
		}
	}
	
	public void onLogout()
	{
		_isOnline = false;
		_client = null;
	}
	
	public void sendPacket(final String sender, final String msg)
	{
		_client.sendPacket(sender, msg);
	}
	
	public void sendPacket(final PacketWriter packet)
	{
		_client.sendPacket(packet);
	}
	
	public void saveGameStat(final GameStat stat)
	{
		// TODO
		/*try
		{
			final PreparedStatement s = Database.getInstance().getConnection().prepareStatement("INSERT INTO status VALUES (?, ?, ?, ?, ?)");
			s.setInt(1, userId);
			s.setInt(2, stats.getGameId().ordinal());
			s.setInt(3, stats.getScore());
			s.setInt(4, stats.getWins());
			s.setInt(5, stats.getLoses());
			s.execute();
			s.close();
		}
		catch (SQLException e)
		{
			try
			{
				final PreparedStatement s = Database.getInstance().getConnection().prepareStatement("UPDATE status SET score=?, wins=?, loses=? WHERE gameId=? AND userId=?");
				s.setInt(1, stats.getScore());
				s.setInt(2, stats.getWins());
				s.setInt(3, stats.getLoses());
				s.setInt(4, stats.getGameId().ordinal());
				s.setInt(5, userId);
				s.execute();
				s.close();
			}
			catch (SQLException e1)
			{
				System.out.println("Failed saving game data for userId " + userId + " and gameId " + stats.getGameId() + "!!!");
				e1.printStackTrace();
			}
		}*/
	}
}