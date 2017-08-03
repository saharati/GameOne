package network.request;

import java.util.StringTokenizer;

import handlers.AdminCommandHandler;
import handlers.IAdminCommandHandler;
import network.PacketReader;
import server.objects.AccessLevel;
import server.objects.GameClient;
import util.Broadcast;
import util.StringUtil;

public final class RequestMessage extends PacketReader<GameClient>
{
	private String _message;
	
	@Override
	public void read()
	{
		_message = readString();
	}
	
	@Override
	public void run(final GameClient client)
	{
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
				handler.useCommand(_message, client.getUser());
			else
				client.sendPacket("Server", "No such command, use //list to see available commands.");
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