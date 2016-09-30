package network.request;

import network.PacketReader;
import network.response.WaitingRoomResponse;
import objects.GameId;
import server.objects.GameClient;
import util.Broadcast;

/**
 * Requests list of players from the server for a certain game's waiting room.
 * @author Sahar
 */
public final class RequestWaitingRoom extends PacketReader<GameClient>
{
	private GameId _gameId;
	
	@Override
	public void read()
	{
		_gameId = GameId.values()[readInt()];
	}
	
	@Override
	public void run(final GameClient client)
	{
		Broadcast.toAllUsersOfGame(new WaitingRoomResponse(_gameId), _gameId);
	}
}