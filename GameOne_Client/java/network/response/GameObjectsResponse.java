package network.response;

import java.util.HashMap;
import java.util.Map;

import client.Client;
import mario.SuperMario;
import network.PacketReader;
import objects.GameId;
import objects.mario.MarioObject;
import objects.mario.MarioType;
import objects.pacman.PacmanObject;
import pacman.PacmanBuilder;
import pacman.objects.PacmanMapObject;
import pacman.objects.PacmanMap;
import windows.Startup;

/**
 * Packet collecting objects from the server for specific games upon client login.
 * @author Sahar
 */
public final class GameObjectsResponse extends PacketReader<Client>
{
	private GameId _gameId;
	
	@Override
	public void read()
	{
		_gameId = GameId.values()[readInt()];
	}
	
	@Override
	public void run(final Client client)
	{
		switch (_gameId)
		{
			case MARIO:
				final MarioType[] marioTypes = MarioType.values();
				final MarioObject[] marioObjects = new MarioObject[readInt()];
				for (int i = 0;i < marioObjects.length;i++)
					marioObjects[i] = new MarioObject(readInt(), readInt(), marioTypes[readInt()]);
				
				Client.getInstance().setMarioObjects(marioObjects);
				SuperMario.getInstance().reset();
				break;
			case PACMAN:
				final PacmanObject[] pacmanValues = PacmanObject.values();
				final Map<Integer, PacmanMap> pacmanMaps = new HashMap<>();
				final int mapAmount = readInt();
				for (int i = 0;i < mapAmount;i++)
				{
					final int key = readInt();
					final PacmanMapObject[][] objects = new PacmanMapObject[PacmanBuilder.ARRAY_DIMENSIONS[0]][PacmanBuilder.ARRAY_DIMENSIONS[1]];
					for (int x = 0;x < objects.length;x++)
						for (int y = 0;y < objects[x].length;y++)
							objects[x][y] = new PacmanMapObject(pacmanValues[readInt()]);
					
					pacmanMaps.put(key, new PacmanMap(objects));
				}
				
				Client.getInstance().setPacmanMaps(pacmanMaps);
				PacmanBuilder.getInstance().getSelectionPanel().reloadComboBox();
				break;
		}
		
		if (Client.getInstance().ready())
			((Startup) Client.getInstance().getCurrentWindow()).progress();
	}
}