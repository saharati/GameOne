package server.network.incoming.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.network.GameClient;
import server.network.incoming.IIncomingPacket;
import server.network.incoming.PacketReader;
import server.network.outgoing.impl.LoginResponse;
import util.Broadcast;
import util.configs.Config;
import util.database.Database;

/**
 * RequestLogin packet implementation.
 * @author Sahar
 */
public final class RequestLogin implements IIncomingPacket
{
	private static final Logger LOGGER = Logger.getLogger(RequestLogin.class.getName());
	
	private String _username;
	private String _password;
	
	@Override
	public void read(final GameClient client, final PacketReader packet)
	{
		_username = packet.readString();
		_password = packet.readString();
	}
	
	@Override
	public void run(final GameClient client)
	{
		if (Broadcast.THREADS.size() > Config.MAXIMUM_ONLINE_USERS)
			client.sendPacket(LoginResponse.SERVER_FULL);
		else
		{
			try (final Connection con = Database.getConnection();
				final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE username=?"))
			{
				ps.setString(1, _username);
				
				try (final ResultSet rs = ps.executeQuery())
				{
					if (rs.next())
					{
						if (rs.getString("password").equals(_password))
						{
							// Add user data here, like score etc and then attach user to client.
							client.sendPacket(LoginResponse.LOGIN_OK);
						}
						else
							client.sendPacket(LoginResponse.LOGIN_FAILED);
					}
					else
					{
						if (Config.AUTO_CREATE_ACCOUNTS)
						{
							try (final PreparedStatement ps2 = con.prepareStatement("INSERT INTO accounts VALUES (?, ?, ?, ?, ?)"))
							{
								// Insert user data here.
								// Create new user and add data to it too.
								// Attach the newly created user to client.
								
								client.sendPacket(LoginResponse.LOGIN_OK);
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