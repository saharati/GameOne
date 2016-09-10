package client.network.incoming;

import javax.swing.JOptionPane;

import client.Client;
import client.network.IIncomingPacket;
import network.PacketReader;
import windows.GameSelect;
import windows.Login;

/**
 * LoginResponse packet implementation.
 * @author Sahar
 */
public final class LoginResponse implements IIncomingPacket
{
	private static final byte LOGIN_OK = 1;
	private static final byte LOGIN_FAILED = -1;
	private static final byte SERVER_FULL = -2;
	private static final byte SERVER_ERROR = -3;
	
	private byte _result;
	
	@Override
	public void read(final Client client, final PacketReader packet)
	{
		_result = packet.readByte();
	}
	
	@Override
	public void run(final Client client)
	{
		switch (_result)
		{
			case LOGIN_OK:
				client.storeUserDetails();
				
				Login.getInstance().setVisible(false);
				GameSelect.getInstance().setVisible(true);
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
		}
	}
}