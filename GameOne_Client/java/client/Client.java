package client;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import network.BasicClient;
import network.PacketInfo;
import network.PacketReader;
import objects.mario.MarioObject;
import pacman.objects.PacmanMap;
import windows.Startup;

/**
 * Class holding info regarding client such as current socket, records, user etc.
 * @author Sahar
 */
public final class Client extends BasicClient
{
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	
	private String _username;
	private String _password;
	private MarioObject[] _marioObjects;
	private Map<Integer, PacmanMap> _pacmanMaps;
	private JFrame _currentWindow;
	
	public void setStartupWindow(final Startup startup)
	{
		_currentWindow = startup;
	}
	
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
	
	public void setMarioObjects(final MarioObject[] marioObjects)
	{
		_marioObjects = marioObjects;
		
		LOGGER.info("Received " + marioObjects.length + " mario objects from server.");
	}
	
	public MarioObject[] getMarioObjects()
	{
		return _marioObjects;
	}
	
	public void setPacmanMaps(final Map<Integer, PacmanMap> pacmanMaps)
	{
		_pacmanMaps = pacmanMaps;
		
		LOGGER.info("Received " + pacmanMaps.size() + " pacman maps from server.");
	}
	
	public Map<Integer, PacmanMap> getPacmanMaps()
	{
		return _pacmanMaps;
	}
	
	public boolean ready()
	{
		return _marioObjects != null && _pacmanMaps != null && _currentWindow instanceof Startup;
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
			packet.read();
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