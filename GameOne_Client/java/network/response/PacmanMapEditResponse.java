package network.response;

import javax.swing.JOptionPane;

import client.Client;
import network.PacketReader;
import pacman.MapBuilder;

/**
 * Response from the server about a map edit request.
 * @author Sahar
 */
public final class PacmanMapEditResponse extends PacketReader<Client>
{
	private byte _response;
	
	@Override
	public void read()
	{
		_response = readByte();
	}
	
	@Override
	public void run(final Client client)
	{
		switch (_response)
		{
			case -1:
				JOptionPane.showMessageDialog(null, "You do not have permissions to save new maps.", "Fail", JOptionPane.ERROR_MESSAGE);
				break;
			case -2:
				JOptionPane.showMessageDialog(null, "Your map must have at least 1 star and a player.", "Fail", JOptionPane.ERROR_MESSAGE);
				break;
			case 1:
				JOptionPane.showMessageDialog(null, "Map saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
				
				MapBuilder.getInstance().reset();
				break;
		}
	}
}