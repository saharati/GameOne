package network.response;

import chess.ChessScreen;
import client.Client;
import network.PacketReader;
import objects.GameId;
import windows.WaitingRoom;

/**
 * Get parameters for starting a specific multiplayer game.
 * @author Sahar
 */
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
			case CHESS:
				client.setCurrentDetails(ChessScreen.getInstance(), GameId.CHESS, false);
				
				ChessScreen.getInstance().start(_isStarting ? "white" : "black");
				break;
		}
	}
}