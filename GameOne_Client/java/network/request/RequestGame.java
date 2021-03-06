package network.request;

import client.Client;
import network.PacketInfo;
import network.PacketWriter;

public final class RequestGame extends PacketWriter
{
	@Override
	public void write()
	{
		writeInt(PacketInfo.GAME.ordinal());
		
		if (Client.getInstance().getCurrentGame() == null)
			writeInt(-1);
		else
			writeInt(Client.getInstance().getCurrentGame().ordinal());
	}
}