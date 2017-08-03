package network.response;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;

public final class TurnChangeResponse extends PacketWriter
{
	private final GameId _gameId;
	
	// Chess
	private String[] _images;
	
	// Slide a Lama
	private String[][] _matrixPanel;
	private String[] _nextCards;
	private int _score;
	
	// Checkers
	private String _image;
	
	// Checkers && Chess
	private int[][] _positions;
	
	public TurnChangeResponse(final String[] images, final int[][] positions)
	{
		_gameId = GameId.CHESS_MP;
		_images = images;
		_positions = positions;
	}
	
	public TurnChangeResponse(final String[][] matrixPanel, final String[] nextCards, final int score)
	{
		_gameId = GameId.LAMA;
		_matrixPanel = matrixPanel;
		_nextCards = nextCards;
		_score = score;
	}
	
	public TurnChangeResponse(final String image, final int[][] positions)
	{
		_gameId = GameId.CHECKERS;
		_image = image;
		_positions = positions;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.TURN.ordinal());
		
		switch (_gameId)
		{
			case CHESS_MP:
				for (int i = 0;i < _images.length;i++)
					writeString(_images[i]);
				for (int i = 0;i < _positions.length;i++)
					for (int j = 0;j < _positions[i].length;j++)
						writeInt(_positions[i][j]);
				break;
			case LAMA:
				for (int i = 0;i < _matrixPanel.length;i++)
					for (int j = 0;j < _matrixPanel[i].length;j++)
						writeString(_matrixPanel[i][j]);
				for (int i = 0;i < _nextCards.length;i++)
					writeString(_nextCards[i]);
				writeInt(_score);
				break;
			case CHECKERS:
				writeString(_image);
				for (int i = 0;i < _positions.length;i++)
					for (int j = 0;j < _positions[i].length;j++)
						writeInt(_positions[i][j]);
				break;
		}
	}
}