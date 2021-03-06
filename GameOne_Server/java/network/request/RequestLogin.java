package network.request;

import data.UsersTable;
import network.PacketReader;
import network.response.LoginResponse;
import server.objects.GameClient;
import server.objects.User;

public final class RequestLogin extends PacketReader<GameClient>
{
	private String _username;
	private String _password;
	private String _mac;
	
	@Override
	public void read()
	{
		_username = readString();
		_password = readString();
		_mac = readString();
	}
	
	@Override
	public void run(final GameClient client)
	{
		if (_username.isEmpty() || _username.contains(" ") || _password.isEmpty())
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