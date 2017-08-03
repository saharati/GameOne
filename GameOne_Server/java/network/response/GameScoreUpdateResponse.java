package network.response;

import data.UsersTable;
import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;
import objects.GameResult;

public final class GameScoreUpdateResponse extends PacketWriter
{
	private final GameResult _result;
	private final GameId _gameId;
	
	public GameScoreUpdateResponse(final GameResult result, final GameId gameId)
	{
		_result = result;
		_gameId = gameId;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.SCORE.ordinal());
		
		writeInt(_result.ordinal());
		
		final String[][] toplist = UsersTable.getInstance().getTop5(_gameId);
		for (int i = 0;i < toplist.length;i++)
			for (int j = 0;j < toplist[i].length;j++)
				writeString(toplist[i][j]);
	}
}