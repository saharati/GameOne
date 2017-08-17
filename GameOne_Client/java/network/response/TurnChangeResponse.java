package network.response;

import checkers.CheckersScreen;
import chess.ChessBoard;
import client.Client;
import network.PacketReader;
import sal.SalScreen;

public final class TurnChangeResponse extends PacketReader<Client>
{
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
	
	@Override
	public void read()
	{
		switch (Client.getInstance().getCurrentGame())
		{
			case CHESS_MP:
				_images = new String[2];
				_positions = new int[2][4];
				for (int i = 0;i < _images.length;i++)
					_images[i] = readString();
				for (int i = 0;i < _positions.length;i++)
					for (int j = 0;j < _positions[i].length;j++)
						_positions[i][j] = readInt();
				break;
			case LAMA:
				_matrixPanel = new String[5][5];
				_nextCards = new String[3];
				for (int i = 0;i < _matrixPanel.length;i++)
					for (int j = 0;j < _matrixPanel[i].length;j++)
						_matrixPanel[i][j] = readString();
				for (int i = 0;i < _nextCards.length;i++)
					_nextCards[i] = readString();
				_score = readInt();
				break;
			case CHECKERS:
				_image = readString();
				_positions = new int[3][2];
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
			case CHESS_MP:
				ChessBoard.getInstance().updateData(_images, _positions);
				break;
			case LAMA:
				if (!SalScreen.getInstance().isVisible())
					SalScreen.getInstance().start(_matrixPanel, _nextCards);
				else
					SalScreen.getInstance().updateData(_matrixPanel, _nextCards, _score);
				break;
			case CHECKERS:
				CheckersScreen.getInstance().updateData(_image, _positions);
				break;
		}
	}
}