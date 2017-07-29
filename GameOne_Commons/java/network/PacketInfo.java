package network;

import java.lang.reflect.InvocationTargetException;

public enum PacketInfo
{
	LOGIN("RequestLogin", "LoginResponse"),
	MESSAGE("RequestMessage", "MessageResponse"),
	LOGOUT("RequestLogout", "LogoutResponse"),
	GAME("RequestGame", "GameResponse"),
	OBJECTS("RequestGameObjects", "GameObjectsResponse"),
	EDIT("RequestGameEdit", "GameEditResponse"),
	SCORE("RequestUpdateGameScore", "GameScoreUpdateResponse"),
	WAIT("RequestWaitingRoom", "WaitingRoomResponse"),
	INVITE("RequestInviteToDuel", "DuelInviteResponse"),
	START("RequestGameStart", "GameStartResponse"),
	TURN("RequestTurnChange", "TurnChangeResponse"),
	CONFIGS("RequestGameConfigs", "GameConfigsResponse");
	
	private static final String REQUEST_PACKAGE = "network.request";
	private static final String RESPONSE_PACKAGE = "network.response";
	
	private final PacketReader<BasicClient> _readPacket;
	
	@SuppressWarnings("unchecked")
	private PacketInfo(final String requestPacket, final String responsePacket)
	{
		try
		{
			Class<?> packet = Class.forName(REQUEST_PACKAGE + "." + requestPacket);
			if (!PacketReader.class.isAssignableFrom(packet))
				packet = Class.forName(RESPONSE_PACKAGE + "." + responsePacket);
			
			_readPacket = (PacketReader<BasicClient>) packet.getConstructor().newInstance();
		}
		catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public PacketReader<BasicClient> getReadPacket()
	{
		return _readPacket;
	}
}