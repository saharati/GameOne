package network.request;

import client.Client;
import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

/**
 * Requests a turn change for a specific MP game.
 * @author Sahar
 */
public final class RequestTurnChange extends PacketWriter
{
	private final GameId _gameId;
	
	// Chess
	private final String[] _images;
	private final int[][] _positions;
	
	public RequestTurnChange(final String[] images, final int[][] positions)
	{
		_gameId = GameId.CHESS;
		_images = images;
		_positions = positions;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.TURN.ordinal());
		
		writeInt(_gameId.ordinal());
		switch (Client.getInstance().getCurrentGame())
		{
			case CHESS:
				for (int i = 0;i < _images.length;i++)
					writeString(_images[i]);
				for (int i = 0;i < _positions.length;i++)
					for (int j = 0;j < _positions[i].length;j++)
						writeInt(_positions[i][j]);
				break;
		}
	}
}