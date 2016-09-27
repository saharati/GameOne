package network.response;

import javax.swing.JOptionPane;

import client.Client;
import mario.SuperMario;
import network.PacketReader;
import objects.GameId;
import pacman.PacmanBuilder;

/**
 * Response from the server about a game edit request.
 * @author Sahar
 */
public final class GameEditResponse extends PacketReader<Client>
{
	private static final byte NO_PERMISSION = -1;
	private static final byte FAIL = -2;
	private static final byte SUCCESS = 1;
	
	private GameId _gameId;
	private byte _response;
	
	@Override
	public void read()
	{
		_gameId = GameId.values()[readInt()];
		_response = readByte();
	}
	
	@Override
	public void run(final Client client)
	{
		switch (_gameId)
		{
			case MARIO:
				SuperMario.getInstance().getSelectionPanel().enableAllButtons();
				
				if (_response == NO_PERMISSION)
					JOptionPane.showMessageDialog(null, "You do not have permissions to edit games.", "Fail", JOptionPane.ERROR_MESSAGE);
				else if (_response == FAIL)
					JOptionPane.showMessageDialog(null, "Number of players cannot be different than 1.", "Fail", JOptionPane.ERROR_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "Changes saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
				break;
			case PACMAN:
				if (_response == SUCCESS)
				{
					PacmanBuilder.getInstance().reset();
					
					JOptionPane.showMessageDialog(null, "Changes saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
				}
				else if (_response == FAIL)
					JOptionPane.showMessageDialog(null, "Your map must have at least 1 star and a player.", "Fail", JOptionPane.ERROR_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "You do not have permissions to edit games.", "Fail", JOptionPane.ERROR_MESSAGE);
				break;
		}
	}
}