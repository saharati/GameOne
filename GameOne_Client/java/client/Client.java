package client;

import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import network.BasicClient;
import network.PacketInfo;
import network.PacketReader;
import network.request.RequestGame;
import objects.GameId;
import objects.mario.MarioObject;
import pacman.objects.PacmanMap;
import windows.Startup;

/**
 * Class holding info regarding client such as current socket, records, user etc.
 * @author Sahar
 */
public final class Client extends BasicClient
{
	private String _username;
	private String _password;
	
	private MarioObject[] _marioObjects;
	private Map<Integer, PacmanMap> _pacmanMaps;
	
	private GameId _currentGame;
	private JFrame _currentWindow;
	
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
	
	public void setCurrentDetails(final JFrame window, final GameId gameId, final boolean sendPacket)
	{
		_currentGame = gameId;
		_currentWindow = window;
		
		if (sendPacket)
			sendPacket(new RequestGame());
	}
	
	public JFrame getCurrentWindow()
	{
		return _currentWindow;
	}
	
	public GameId getCurrentGame()
	{
		return _currentGame;
	}
	
	@Override
	public void readPacket()
	{
		_readBuffer.flip();
		while (_readBuffer.hasRemaining())
		{
			final int opCode = _readBuffer.getInt();
			final PacketInfo inf = PacketInfo.values()[opCode];
			final PacketReader<BasicClient> packet = inf.getReadPacket();
			
			packet.setBuffer(_readBuffer);
			packet.read();
			packet.run(this);
		}
		_readBuffer.clear();
		
		getChannel().read(_readBuffer, this, _readHandler);
	}
	
	@Override
	public void onDisconnect()
	{
		JOptionPane.showMessageDialog(null, "You have been disconnected from the server.", "Server Crashed", JOptionPane.ERROR_MESSAGE);
		
		System.exit(0);
	}
	
	public static Client getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final Client INSTANCE = new Client();
	}
}