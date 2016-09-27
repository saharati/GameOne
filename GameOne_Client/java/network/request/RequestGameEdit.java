package network.request;

import java.awt.Component;
import java.util.List;

import mario.objects.AbstractObject;
import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;
import pacman.PacmanButton;

/**
 * Packet sending game edit request to the server.
 * @author Sahar
 */
public final class RequestGameEdit extends PacketWriter
{
	private final GameId _gameId;
	
	// Mario
	private List<Component> _addedObjects;
	private List<Component> _removedObjects;
	
	// Pacman
	private int _mapId;
	private PacmanButton[][] _buttons;
	
	public RequestGameEdit(final List<Component> addedObjects, final List<Component> removedObjects)
	{
		_gameId = GameId.MARIO;
		_addedObjects = addedObjects;
		_removedObjects = removedObjects;
	}
	
	public RequestGameEdit(final int mapId, final PacmanButton[][] buttons)
	{
		_gameId = GameId.PACMAN;
		_mapId = mapId;
		_buttons = buttons;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.EDIT.ordinal());
		
		writeInt(_gameId.ordinal());
		switch (_gameId)
		{
			case MARIO:
				writeInt(_addedObjects.size());
				for (final Component comp : _addedObjects)
				{
					final AbstractObject obj = (AbstractObject) comp;
					
					writeInt(obj.getInitialX());
					writeInt(obj.getInitialY());
					writeInt(obj.getInitialType().ordinal());
				}
				writeInt(_removedObjects.size());
				for (final Component comp : _removedObjects)
				{
					final AbstractObject obj = (AbstractObject) comp;
					
					writeInt(obj.getInitialX());
					writeInt(obj.getInitialY());
					writeInt(obj.getInitialType().ordinal());
				}
				break;
			case PACMAN:
				writeInt(_mapId);
				for (int i = 0;i < _buttons.length;i++)
					for (int j = 0;j < _buttons[i].length;j++)
						writeInt(_buttons[i][j].getKey().ordinal());
				break;
		}
	}
}