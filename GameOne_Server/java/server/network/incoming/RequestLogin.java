package server.network.incoming;

import data.sql.UsersTable;
import network.IIncomingPacket;
import network.PacketReader;
import server.network.outgoing.LoginResponse;
import server.objects.GameClient;
import server.objects.User;

/**
 * RequestLogin packet implementation.
 * @author Sahar
 */
public final class RequestLogin implements IIncomingPacket<GameClient>
{
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
		
		final LoginResponse response = UsersTable.getInstance().tryToLogin(_username, _password, client.getRemoteAddress().toString(), _mac);
		if (response == LoginResponse.LOGIN_OK)
		{
			final User user = UsersTable.getInstance().getUserByName(_username);
			client.setUser(user);
			user.onLogin(client, client.getRemoteAddress().toString(), _mac);
		}
		
		client.sendPacket(response);
	}
}