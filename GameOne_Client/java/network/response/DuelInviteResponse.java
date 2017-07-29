package network.response;

import javax.swing.JOptionPane;

import client.Client;
import network.PacketReader;
import objects.DuelInviteResult;
import windows.WaitingRoom;

/**
 * Get a response for a duel invitation.
 * @author Sahar
 */
public final class DuelInviteResponse extends PacketReader<Client>
{
	private DuelInviteResult _response;
	private String _username;
	
	@Override
	public void read()
	{
		_response = DuelInviteResult.values()[readInt()];
		_username = readString();
	}
	
	@Override
	public void run(final Client client)
	{
		switch (_response)
		{
			case CANNOT_INVITE_SELF:
				JOptionPane.showMessageDialog(null, "You cannot invite yourself to a duel.", "Fail", JOptionPane.ERROR_MESSAGE);
				break;
			case TARGET_UNAVAILABLE:
				JOptionPane.showMessageDialog(null, "This target is currently unavailable.", "Fail", JOptionPane.ERROR_MESSAGE);
				break;
			case CANCELLED:
				WaitingRoom.getInstance().cancelAllDialogs();
				break;
			case WAIT:
				WaitingRoom.getInstance().showWaitDialog();
				break;
			case RESPOND:
				WaitingRoom.getInstance().showAskDialog(_username);
				break;
		}
	}
}