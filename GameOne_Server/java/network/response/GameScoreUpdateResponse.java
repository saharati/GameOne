package network.response;

import data.sql.UsersTable;
import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

/**
 * Packet used to present recent toplist in a specific game to the client.
 * @author Sahar
 */
public final class GameScoreUpdateResponse extends PacketWriter
{
	private final GameId _gameId;
	
	public GameScoreUpdateResponse(final GameId gameId)
	{
		_gameId = gameId;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.SCORE.ordinal());
		
		final String[][] toplist = UsersTable.getInstance().getTop5(_gameId);
		for (int i = 0;i < toplist.length;i++)
			for (int j = 0;j < toplist[i].length;j++)
				writeString(toplist[i][j]);
	}
}