package network.response;

import client.Client;
import network.PacketReader;
import windows.WaitingRoom;

/**
 * Shows up the waiting room window and updates user list.
 * @author Sahar
 */
public final class WaitingRoomResponse extends PacketReader<Client>
{
	private Object[][] _userList;
	
	@Override
	public void read()
	{
		_userList = new Object[readInt()][3];
		for (int i = 0;i < _userList.length;i++)
		{
			_userList[i][0] = readString();
			_userList[i][1] = readBoolean() ? "Dueling" : "Available";
			_userList[i][2] = readInt() + " / " + readInt() + " / " + readInt();
		}
	}
	
	@Override
	public void run(final Client client)
	{
		if (client.getCurrentWindow() == WaitingRoom.getInstance())
			WaitingRoom.getInstance().reload(_userList);
	}
}