package network.response;

import javax.swing.JOptionPane;

import client.Client;
import network.PacketReader;
import windows.GameSelect;

/**
 * Packet holding the response for a login request.
 * @author Sahar
 */
public final class LoginResponse extends PacketReader<Client>
{
	private static final byte LOGIN_OK = 1;
	private static final byte LOGIN_FAILED = -1;
	private static final byte SERVER_FULL = -2;
	private static final byte SERVER_ERROR = -3;
	private static final byte ALREADY_ONLINE = -4;
	private static final byte USER_BANNED = -5;
	
	private byte _result;
	
	@Override
	public void read()
	{
		_result = readByte();
	}
	
	@Override
	public void run(final Client client)
	{
		switch (_result)
		{
			case LOGIN_OK:
				client.storeUserDetails();
				client.setCurrentWindow(GameSelect.getInstance());
				break;
			case LOGIN_FAILED:
				JOptionPane.showMessageDialog(null, "Username or password incorrect.", "Login Failed", JOptionPane.ERROR_MESSAGE);
				break;
			case SERVER_FULL:
				JOptionPane.showMessageDialog(null, "Server is full, please try again later.", "Login Failed", JOptionPane.ERROR_MESSAGE);
				break;
			case SERVER_ERROR:
				JOptionPane.showMessageDialog(null, "There was an error connecting to the server.", "Login Failed", JOptionPane.ERROR_MESSAGE);
				break;
			case ALREADY_ONLINE:
				JOptionPane.showMessageDialog(null, "User already online, access denied.", "Login Failed", JOptionPane.ERROR_MESSAGE);
				break;
			case USER_BANNED:
				JOptionPane.showMessageDialog(null, "This user is banned, access denied.", "Login Failed", JOptionPane.ERROR_MESSAGE);
				break;
		}
	}
}