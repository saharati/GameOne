package server.network.incoming;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import network.IIncomingPacket;
import network.PacketReader;
import server.network.outgoing.LoginResponse;
import server.objects.GameClient;
import server.objects.GameStat;
import server.objects.User;
import util.Broadcast;
import util.StringUtil;
import util.configs.Config;
import util.database.Database;

/**
 * RequestLogin packet implementation.
 * @author Sahar
 */
public final class RequestLogin implements IIncomingPacket<GameClient>
{
	private static final Logger LOGGER = Logger.getLogger(RequestLogin.class.getName());
	
	private static final String SELECT_USER = "SELECT id, password, lastIp, lastMac, isGM FROM users WHERE username=?";
	private static final String SELECT_GAMES = "SELECT gameId, score, wins, loses FROM user_games WHERE userId=?";
	private static final String UPDATE_USER = "UPDATE users SET lastIp=?, lastMac=? WHERE id=?";
	private static final String INSERT_USER = "INSERT INTO users (username, password, lastIp, lastMac, isGM) VALUES (?, ?, ?, ?, ?)";
	private static final String SELECT_COUNT = "SELECT COUNT(*) FROM users";
	
	private String _username;
	private String _password;
	private String _mac;
	
	@Override
	public void read(final GameClient client, final PacketReader packet)
	{
		_username = packet.readString();
		_password = packet.readString();
		_mac = packet.readString();
	}
	
	@Override
	public void run(final GameClient client)
	{
		if (_username == null || _password == null || _mac == null)
			return;
		
		_username = _username.trim();
		_password = _password.trim();
		if (_username.isEmpty() || _password.isEmpty())
			return;
		
		if (Broadcast.THREADS.size() > Config.MAXIMUM_ONLINE_USERS)
			client.sendPacket(LoginResponse.SERVER_FULL);
		else
		{
			try (final Connection con = Database.getConnection();
				final PreparedStatement ps = con.prepareStatement(SELECT_USER))
			{
				ps.setString(1, _username);
				
				try (final ResultSet rs = ps.executeQuery())
				{
					if (rs.next())
					{
						if (rs.getString("password").equals(_password))
						{
							final User user = new User(rs.getInt("id"), _username, rs.getInt("isGM") == 1, client);
							try (final PreparedStatement ps2 = con.prepareStatement(SELECT_GAMES))
							{
								ps2.setInt(1, user.getId());
								
								try (final ResultSet rs2 = ps2.executeQuery())
								{
									while (rs2.next())
									{
										final GameStat stat = new GameStat(rs2.getInt("gameId"), rs2.getInt("score"), rs2.getInt("wins"), rs2.getInt("loses"));
										
										user.getGameStats().put(stat.getGameId(), stat);
									}
								}
							}
							try (final PreparedStatement ps2 = con.prepareStatement(UPDATE_USER))
							{
								ps2.setString(1, client.getRemoteAddress().toString());
								ps2.setString(2, _mac);
								ps2.setInt(3, user.getId());
								ps2.execute();
							}
							
							final String msg = StringUtil.refineBeforeSend("Server", user.getName() + " has logged on.");
							Broadcast.toAllUsers(msg);
							
							client.setUser(user);
							client.sendPacket(LoginResponse.LOGIN_OK);
							client.onEnter();
						}
						else
							client.sendPacket(LoginResponse.LOGIN_FAILED);
					}
					else
					{
						if (Config.AUTO_CREATE_ACCOUNTS)
						{
							boolean setGM = false;
							try (final PreparedStatement ps2 = con.prepareStatement(SELECT_COUNT))
							{
								try (final ResultSet rs2 = ps2.executeQuery())
								{
									rs2.next();
									if (rs2.getInt(1) == 0)
										setGM = true;
								}
							}
							try (final PreparedStatement ps2 = con.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS))
							{
								ps2.setString(1, _username);
								ps2.setString(2, _password);
								ps2.setString(3, client.getRemoteAddress().toString());
								ps2.setString(4, _mac);
								ps2.setInt(5, setGM ? 1 : 0);
								ps2.executeUpdate();
								
								try (final ResultSet rs2 = ps2.getGeneratedKeys())
								{
									if (rs2.next())
									{
										final User user = new User(rs2.getInt(1), _username, setGM, client);
										final String msg = StringUtil.refineBeforeSend("Server", user.getName() + " has logged on.");
										Broadcast.toAllUsers(msg);
										
										client.setUser(user);
										client.sendPacket(LoginResponse.LOGIN_OK);
										client.onEnter();
									}
									else
										client.sendPacket(LoginResponse.SERVER_ERROR);
								}
							}
						}
						else
							client.sendPacket(LoginResponse.LOGIN_FAILED);
					}
				}
			}
			catch (final SQLException e)
			{
				LOGGER.log(Level.WARNING, "Failed reading login packet: ", e);
				
				client.sendPacket(LoginResponse.SERVER_ERROR);
			}
		}
	}
}