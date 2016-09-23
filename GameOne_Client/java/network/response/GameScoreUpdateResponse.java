package network.response;

import client.Client;
import network.PacketReader;
import windows.Top;

/**
 * Packet responsible for displaying out top lists for games.
 * @author Sahar
 */
public final class GameScoreUpdateResponse extends PacketReader<Client>
{
	private String[][] _toplist = new String[5][4];
	
	@Override
	public void read()
	{
		for (int i = 0;i < _toplist.length;i++)
			for (int j = 0;j < _toplist[i].length;j++)
				_toplist[i][j] = readString();
	}
	
	@Override
	public void run(final Client client)
	{
		new Top(_toplist).setVisible(true);
	}
}