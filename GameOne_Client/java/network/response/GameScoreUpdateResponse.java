package network.response;

import chess.ChessScreen;
import client.Client;
import network.PacketReader;
import objects.GameResult;
import windows.Top;

/**
 * Packet responsible for displaying out top lists for games.
 * @author Sahar
 */
public final class GameScoreUpdateResponse extends PacketReader<Client>
{
	private GameResult _result;
	private String[][] _toplist = new String[5][4];
	
	@Override
	public void read()
	{
		_result = GameResult.values()[readInt()];
		for (int i = 0;i < _toplist.length;i++)
			for (int j = 0;j < _toplist[i].length;j++)
				_toplist[i][j] = readString();
	}
	
	@Override
	public void run(final Client client)
	{
		switch (client.getCurrentGame())
		{
			case CHESS:
				ChessScreen.getInstance().showResult(_result);
				break;
		}
		if (_result != GameResult.EXIT)
			new Top(_toplist).setVisible(true);
	}
}