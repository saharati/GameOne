package network.response;

import javax.swing.JTextArea;

import client.Client;
import configs.Config;
import network.PacketReader;
import util.Sounds;
import windows.GameSelect;

/**
 * Incoming chat message from the server.
 * @author Sahar
 */
public final class MessageResponse extends PacketReader<Client>
{
	private String _message;
	
	@Override
	public void read()
	{
		_message = readString();
	}
	
	@Override
	public void run(final Client client)
	{
		if (Config.CHAT_SOUND && !_message.substring(9, 15).equals("Server"))
			Sounds.playSound(1000, 100);
		
		final JTextArea textArea = GameSelect.getInstance().getChatWindow();
		if (Config.CHAT_DIRECTION == 1)
		{
			if (!textArea.getText().isEmpty())
				_message = "\r\n" + _message;
			
			textArea.append(_message);
		}
		else
		{
			if (!textArea.getText().isEmpty())
				_message += "\r\n";
			
			textArea.setText(_message + textArea.getText());
		}
		
		if (Config.MOVE_CARET)
			GameSelect.getInstance().getChatWindow().setCaretPosition(GameSelect.getInstance().getChatWindow().getDocument().getLength());
	}
}