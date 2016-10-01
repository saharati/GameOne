package network.response;

import chess.ChessScreen;
import client.Client;
import network.PacketReader;

/**
 * Get turn change with attached information from the server.
 * @author Sahar
 */
public final class TurnChangeResponse extends PacketReader<Client>
{
	// Chess
	private String[] _images;
	private int[][] _positions;
	
	@Override
	public void read()
	{
		switch (Client.getInstance().getCurrentGame())
		{
			case CHESS:
				_images = new String[2];
				_positions = new int[2][4];
				for (int i = 0;i < _images.length;i++)
					_images[i] = readString();
				for (int i = 0;i < _positions.length;i++)
					for (int j = 0;j < _positions[i].length;j++)
						_positions[i][j] = readInt();
				break;
		}
	}
	
	@Override
	public void run(final Client client)
	{
		switch (client.getCurrentGame())
		{
			case CHESS:
				ChessScreen.getInstance().getBoard().updateData(_images, _positions);
				break;
		}
	}
}