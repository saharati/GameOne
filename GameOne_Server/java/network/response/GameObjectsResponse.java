package network.response;

import java.util.Map.Entry;

import data.sql.MarioTable;
import data.sql.PacmanTable;
import network.PacketInfo;
import network.PacketWriter;
import objects.GameId;
import objects.MarioObject;

/**
 * Outgoing GameObjectsResponse packet implementation.
 * @author Sahar
 */
public final class GameObjectsResponse extends PacketWriter
{
	private final GameId _gameId;
	
	public GameObjectsResponse(final GameId gameId)
	{
		_gameId = gameId;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.OBJECTS.ordinal());
		
		writeInt(_gameId.ordinal());
		
		switch (_gameId)
		{
			case MARIO:
				writeInt(MarioTable.getInstance().getObjects().size());
				for (final MarioObject obj : MarioTable.getInstance().getObjects())
				{
					writeInt(obj.getX());
					writeInt(obj.getY());
					writeString(obj.getType());
				}
				break;
			case PACMAN:
				writeInt(PacmanTable.getInstance().getMaps().size());
				for (Entry<Integer, String[][]> entry : PacmanTable.getInstance().getMaps().entrySet())
				{
					writeInt(entry.getKey());
					
					final String[][] objects = entry.getValue();
					for (int i = 0;i < objects.length;i++)
						for (int j = 0;j < objects[i].length;j++)
							writeString(objects[i][j]);
				}
				break;
		}
	}
}