package network.response;

import java.util.HashMap;
import java.util.Map;

import client.Client;
import network.PacketReader;
import objects.GameId;
import objects.MarioObject;
import windows.Login;

/**
 * GameObjectsResponse packet implementation.
 * @author Sahar
 */
public final class GameObjectsResponse extends PacketReader<Client>
{
	private int _gameId;
	
	@Override
	public void read(final Client client)
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
				final Map<Integer, String[][]> pacmanMaps = new HashMap<>();
				final int mapAmount = readInt();
				for (int i = 0;i < mapAmount;i++)
				{
					final int key = readInt();
					final String[][] objects = new String[16][12];
					for (int x = 0;x < objects.length;x++)
						for (int y = 0;y < objects[x].length;y++)
							objects[x][y] = readString();
					
					pacmanMaps.put(key, objects);
				}
				
				Client.getInstance().setPacmanMaps(pacmanMaps);
				break;
		}
		
		if (Client.getInstance().ready())
			Client.getInstance().setCurrentWindow(Login.getInstance());
	}
}