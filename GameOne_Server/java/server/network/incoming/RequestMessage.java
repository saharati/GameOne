package server.network.incoming;

import network.PacketReader;
import server.network.IIncomingPacket;
import server.objects.GameClient;
import util.Broadcast;
import util.StringUtil;

/**
 * RequestMessage packet implementation.
 * @author Sahar
 */
public final class RequestMessage implements IIncomingPacket
{
	private String _message;
	
	@Override
	public void read(final GameClient client, final PacketReader packet)
	{
		_message = packet.readString();
	}
	
	@Override
	public void run(final GameClient client)
	{
		if (_message.length() > 20)
			return;
		
		_message = StringUtil.refineBeforeSend(client.getUser().getName(), _message);
		
		Broadcast.toAllUsers(_message);
	}
}