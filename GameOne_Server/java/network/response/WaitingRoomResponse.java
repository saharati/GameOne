package network.response;

import java.util.List;
import java.util.stream.Collectors;

import data.sql.UsersTable;
import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;
import server.objects.GameStat;
import server.objects.User;

/**
 * Packet responds with list of players for game's waiting room.
 * @author Sahar
 */
public final class WaitingRoomResponse extends PacketWriter
{
	private final GameId _gameId;
	
	public WaitingRoomResponse(final GameId gameId)
	{
		_gameId = gameId;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.WAIT.ordinal());
		
		final List<User> users = UsersTable.getInstance().getOnlineUsers().filter(u -> u.getCurrentGame() == _gameId).collect(Collectors.toList());
		writeInt(users.size());
		
		users.forEach(u ->
		{
			writeString(u.getUsername());
			writeBoolean(u.isInGroup());
			
			final GameStat stat = u.getGame(_gameId);
			if (stat == null)
			{
				writeInt(0);
				writeInt(0);
				writeInt(0);
			}
			else
			{
				writeInt(stat.getScore());
				writeInt(stat.getWins());
				writeInt(stat.getLoses());
			}
		});
	}
}