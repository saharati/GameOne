package network;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * List of all possible packets.
 * @author Sahar
 */
public enum PacketInfo
{
	LOGIN("RequestLogin", "LoginResponse", false),
	MESSAGE("RequestMessage", "MessageResponse", true),
	LOGOUT("RequestLogout", "LogoutResponse", true),
	GAME("RequestGame", "GameResponse", true),
	OBJECTS("RequestGameObjects", "GameObjectsResponse", false),
	PACMAN_EDIT("RequestPacmanMapEdit", "PacmanMapEditResponse", true);
	
	private static final String REQUEST_PACKAGE = "network.request";
	private static final String RESPONSE_PACKAGE = "network.response";
	
	private final Logger _logger = Logger.getLogger(PacketInfo.class.getName());
	private PacketReader<BasicClient> _readPacket;
	private boolean _authed;
	
	@SuppressWarnings("unchecked")
	private PacketInfo(final String requestPacket, final String responsePacket, final boolean authed)
	{
		try
		{
			Class<?> packet = Class.forName(REQUEST_PACKAGE + "." + requestPacket);
			if (!PacketReader.class.isAssignableFrom(packet))
				packet = Class.forName(RESPONSE_PACKAGE + "." + responsePacket);
			
			_readPacket = (PacketReader<BasicClient>) packet.getConstructor().newInstance();
			_authed = authed;
		}
		catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e)
		{
			_logger.log(Level.WARNING, "Failed initializing PacketInfo: ", e);
		}
	}
	
	public PacketReader<BasicClient> getReadPacket()
	{
		return _readPacket;
	}
	
	public boolean isAuthedState()
	{
		return _authed;
	}
}