package network.response;

import javax.swing.JOptionPane;

import client.Client;
import network.PacketReader;
import objects.LoginResult;
import windows.GameSelect;

/**
 * Packet holding the response for a login request.
 * @author Sahar
 */
public final class LoginResponse extends PacketReader<Client>
{
	private LoginResult _result;
	
	@Override
	public void read()
	{
		_result = LoginResult.values()[readInt()];
	}
	
	@Override
	public void run(final Client client)
	{
		switch (_result)
		{
			case LOGIN_OK:
				client.storeUserDetails();
				client.getCurrentWindow().setVisible(false);
				client.setCurrentDetails(GameSelect.getInstance(), null, false);
				client.getCurrentWindow().setVisible(true);
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