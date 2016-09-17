package client;

import java.nio.ByteBuffer;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import network.BasicClient;
import network.PacketInfo;
import network.PacketReader;
import windows.Login;

/**
 * Class holding info regarding client such as current socket, records, user etc.
 * @author Sahar
 */
public final class Client extends BasicClient
{
	private String _username;
	private String _password;
	private JFrame _currentWindow = Login.getInstance();
	
	public void setLoginDetails(final String username, final String password)
	{
		_username = username;
		_password = password;
	}
	
	public void storeUserDetails()
	{
		final Preferences prop = Preferences.userRoot();
		
		prop.put("user_gameOne", _username);
		prop.put("pass_gameOne", _password);
	}
	
	public void setCurrentWindow(final JFrame window)
	{
		_currentWindow.setVisible(false);
		_currentWindow = window;
		_currentWindow.setVisible(true);
	}
	
	public JFrame getCurrentWindow()
	{
		return _currentWindow;
	}
	
	@Override
	public void readPacket()
	{
		final ByteBuffer buffer = getReadBuffer();
		buffer.flip();
		
		while (buffer.hasRemaining())
		{
			final int opCode = buffer.getInt();
			final PacketInfo inf = PacketInfo.values()[opCode];
			final PacketReader<BasicClient> packet = inf.getReadPacket();
			
			packet.setBuffer(buffer);
			packet.read(this);
			packet.run(this);
		}
		buffer.clear();
		
		getChannel().read(buffer, this, getReadHandler());
	}
	
	@Override
	public void onDisconnect()
	{
		JOptionPane.showMessageDialog(null, "You have been disconnected from the server.", "Server Crashed", JOptionPane.ERROR_MESSAGE);
		
		System.exit(0);
	}
	
	private Client() {}
	
	public static Client getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final Client INSTANCE = new Client();
	}
}