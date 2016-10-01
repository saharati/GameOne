package network.response;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

/**
 * Send turn change data to partner.
 * @author Sahar
 */
public final class TurnChangeResponse extends PacketWriter
{
	private final GameId _gameId;
	
	// Chess
	private final String[] _images;
	private final int[][] _positions;
	
	public TurnChangeResponse(final String[] images, final int[][] positions)
	{
		_gameId = GameId.CHESS;
		_images = images;
		_positions = positions;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.TURN.ordinal());
		
		switch (_gameId)
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