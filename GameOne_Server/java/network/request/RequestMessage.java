package network.request;

import java.util.StringTokenizer;

import handlers.AdminCommandHandler;
import handlers.IAdminCommandHandler;
import network.PacketReader;
import server.objects.AccessLevel;
import server.objects.GameClient;
import util.Broadcast;
import util.StringUtil;

/**
 * RequestMessage packet implementation.
 * @author Sahar
 */
public final class RequestMessage extends PacketReader<GameClient>
{
	private String _message;
	
	@Override
	public void read(final GameClient client)
	{
		_message = readString();
	}
	
	@Override
	public void run(final GameClient client)
	{
		if (_message == null)
			return;
		
		_message = _message.trim();
		if (_message.isEmpty())
			return;
		if (_message.length() > 40)
			return;
		
		if (client.getUser().getAccessLevel() == AccessLevel.GM && _message.startsWith("//"))
		{
			_message = _message.replace("//", "");
			
			final StringTokenizer st = new StringTokenizer(_message, " ");
			final String cmd = st.nextToken();
			final IAdminCommandHandler handler = AdminCommandHandler.getInstance().getHandler(cmd);
			if (handler != null)
			{
				if (handler.useCommand(_message, client.getUser()))
					client.sendPacket("Server", "Command executed succesfully.");
			}
			else
				sendMessage(client);
		}
		else
			sendMessage(client);
	}
	
	private void sendMessage(final GameClient client)
	{
		_message = StringUtil.refineBeforeSend(client.getUser().getUsername(), _message);
		
		Broadcast.toAllUsers(_message);
	}
}