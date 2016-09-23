package network.response;

import java.util.HashMap;
import java.util.Map;

import client.Client;
import network.PacketReader;
import objects.GameId;
import objects.mario.MarioObject;
import objects.pacman.PacmanObject;
import pacman.MapBuilder;
import pacman.objects.MapObject;
import pacman.objects.PacmanMap;
import windows.Startup;

/**
 * Packet collecting objects from the server for specific games upon client login.
 * @author Sahar
 */
public final class GameObjectsResponse extends PacketReader<Client>
{
	private int _gameId;
	
	@Override
	public void read()
	{
		_gameId = readInt();
	}
	
	@Override
	public void run(final Client client)
	{
		final GameId gameId = GameId.values()[_gameId];
		switch (gameId)
		{
			case MARIO:
				final MarioObject[] marioObjects = new MarioObject[readInt()];
				for (int i = 0;i < marioObjects.length;i++)
					marioObjects[i] = new MarioObject(readInt(), readInt(), readString());
				
				Client.getInstance().setMarioObjects(marioObjects);
				break;
			case PACMAN:
				final PacmanObject[] pacmanValues = PacmanObject.values();
				final Map<Integer, PacmanMap> pacmanMaps = new HashMap<>();
				final int mapAmount = readInt();
				for (int i = 0;i < mapAmount;i++)
				{
					final int key = readInt();
					final MapObject[][] objects = new MapObject[MapBuilder.ARRAY_DIMENSIONS[0]][MapBuilder.ARRAY_DIMENSIONS[1]];
					for (int x = 0;x < objects.length;x++)
						for (int y = 0;y < objects[x].length;y++)
							objects[x][y] = new MapObject(pacmanValues[readInt()]);
					
					pacmanMaps.put(key, new PacmanMap(objects));
				}
				
				Client.getInstance().setPacmanMaps(pacmanMaps);
				MapBuilder.getInstance().getSelectionPanel().reloadComboBox();
				break;
		}
		
		if (Client.getInstance().ready())
			((Startup) Client.getInstance().getCurrentWindow()).progress();
	}
}