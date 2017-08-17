package network.response;

import checkers.CheckersScreen;
import chess.ChessScreen;
import client.Client;
import network.PacketReader;
import objects.GameId;
import sal.SalScreen;
import windows.WaitingRoom;

public final class GameStartResponse extends PacketReader<Client>
{
	private boolean _isStarting;
	
	@Override
	public void read()
	{
		_isStarting = readBoolean();
	}
	
	@Override
	public void run(final Client client)
	{
		WaitingRoom.getInstance().cancelAllDialogs();
		
		client.getCurrentWindow().setVisible(false);
		switch (client.getCurrentGame())
		{
			case CHESS_MP:
				client.setCurrentDetails(ChessScreen.getInstance(), GameId.CHESS_MP, false);
				
				ChessScreen.getInstance().start(_isStarting ? "white" : "black", false);
				break;
			case LAMA:
				client.setCurrentDetails(SalScreen.getInstance(), GameId.LAMA, false);
				
				if (_isStarting)
					SalScreen.getInstance().start();
				break;
			case CHECKERS:
				client.setCurrentDetails(CheckersScreen.getInstance(), GameId.CHECKERS, false);
				
				CheckersScreen.getInstance().start(_isStarting ? "white" : "black");
				break;
		}
	}
}