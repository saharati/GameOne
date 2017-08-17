package network.response;

import javax.swing.JOptionPane;

import client.Client;
import mario.SuperMario;
import network.PacketReader;
import objects.GameEditResult;
import pacman.PacmanBuilder;

public final class GameEditResponse extends PacketReader<Client>
{
	private GameEditResult _response;
	
	@Override
	public void read()
	{
		_response = GameEditResult.values()[readInt()];
	}
	
	@Override
	public void run(final Client client)
	{
		switch (client.getCurrentGame())
		{
			case MARIO:
				SuperMario.getInstance().getSelectionPanel().enableAllButtons();
				
				if (_response == GameEditResult.NO_PERMISSION)
					JOptionPane.showMessageDialog(null, "You do not have permissions to edit games.", "Fail", JOptionPane.ERROR_MESSAGE);
				else if (_response == GameEditResult.FAIL)
					JOptionPane.showMessageDialog(null, "Number of players cannot be different than 1.", "Fail", JOptionPane.ERROR_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "Changes saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
				break;
			case PACMAN:
				if (_response == GameEditResult.SUCCESS)
				{
					PacmanBuilder.getInstance().reload();
					
					JOptionPane.showMessageDialog(null, "Changes saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
				}
				else if (_response == GameEditResult.FAIL)
					JOptionPane.showMessageDialog(null, "Your map must have at least 1 star and a player.", "Fail", JOptionPane.ERROR_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "You do not have permissions to edit games.", "Fail", JOptionPane.ERROR_MESSAGE);
				break;
		}
	}
}