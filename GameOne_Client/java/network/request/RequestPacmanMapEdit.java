package network.request;

import network.PacketInfo;
import network.PacketWriter;
import pacman.PacmanButton;

/**
 * Packet sending a map edit request to the server.
 * @author Sahar
 */
public final class RequestPacmanMapEdit extends PacketWriter
{
	private final int _mapId;
	private final PacmanButton[][] _buttons;
	
	public RequestPacmanMapEdit(final int mapId, final PacmanButton[][] buttons)
	{
		_mapId = mapId;
		_buttons = buttons;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.PACMAN_EDIT.ordinal());
		
		writeInt(_mapId);
		for (int i = 0;i < _buttons.length;i++)
			for (int j = 0;j < _buttons[i].length;j++)
				writeInt(_buttons[i][j].getKey().ordinal());
	}
}