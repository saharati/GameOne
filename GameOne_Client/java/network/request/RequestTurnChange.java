package network.request;

import client.Client;
import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;
import sal.SalImage;

/**
 * Requests a turn change for a specific MP game.
 * @author Sahar
 */
public final class RequestTurnChange extends PacketWriter
{
	private final GameId _gameId;
	
	// Chess
	private String[] _images;
	private int[][] _positions;
	
	// Slide a Lama
	private SalImage[][] _matrixPanel;
	private SalImage[] _nextCards;
	private int _score;
	
	public RequestTurnChange(final String[] images, final int[][] positions)
	{
		_gameId = GameId.CHESS;
		_images = images;
		_positions = positions;
	}
	
	public RequestTurnChange(final SalImage[][] matrixPanel, final SalImage[] nextCards, final int score)
	{
		_gameId = GameId.LAMA;
		_matrixPanel = matrixPanel;
		_nextCards = nextCards;
		_score = score;
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
			case LAMA:
				for (int i = 0;i < _matrixPanel.length;i++)
					for (int j = 0;j < _matrixPanel[i].length;j++)
						writeString(_matrixPanel[i][j].getName());
				for (int i = 0;i < _nextCards.length;i++)
					writeString(_nextCards[i].getName());
				writeInt(_score);
				break;
		}
	}
}