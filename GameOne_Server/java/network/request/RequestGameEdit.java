package network.request;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import data.MarioTable;
import data.PacmanTable;
import network.PacketReader;
import network.response.GameObjectsResponse;
import network.response.GameEditResponse;
import objects.GameId;
import objects.mario.MarioObject;
import objects.mario.MarioType;
import objects.pacman.PacmanObject;
import server.objects.AccessLevel;
import server.objects.GameClient;
import util.Broadcast;

public final class RequestGameEdit extends PacketReader<GameClient>
{
	private GameId _gameId;
	
	// Mario
	private MarioObject[] _addedObjects;
	private MarioObject[] _removedObjects;
	
	// Pacman
	private int _mapId;
	private PacmanObject[][] _objects;
	
	@Override
	public void read()
	{
		_gameId = GameId.values()[readInt()];
		switch (_gameId)
		{
			case MARIO:
				final MarioType[] marioValues = MarioType.values();
				
				_addedObjects = new MarioObject[readInt()];
				for (int i = 0;i < _addedObjects.length;i++)
					_addedObjects[i] = new MarioObject(readInt(), readInt(), marioValues[readInt()]);
				_removedObjects = new MarioObject[readInt()];
				for (int i = 0;i < _removedObjects.length;i++)
					_removedObjects[i] = new MarioObject(readInt(), readInt(), marioValues[readInt()]);
				break;
			case PACMAN:
				final PacmanObject[] pacmanValues = PacmanObject.values();
				
				_objects = new PacmanObject[PacmanTable.ARRAY_DIMENSIONS[0]][PacmanTable.ARRAY_DIMENSIONS[1]];
				_mapId = readInt();
				for (int i = 0;i < _objects.length;i++)
					for (int j = 0;j < _objects[i].length;j++)
						_objects[i][j] = pacmanValues[readInt()];
				break;
		}
	}
	
	@Override
	public void run(final GameClient client)
	{
		if (client.getUser().getAccessLevel() != AccessLevel.GM)
			client.sendPacket(GameEditResponse.NO_PERMISSION);
		else
		{
			switch (_gameId)
			{
				case MARIO:
					final List<MarioObject> backup = new CopyOnWriteArrayList<>(MarioTable.getInstance().getObjects());
					for (final MarioObject removedObj : _removedObjects)
					{
						for (final MarioObject existingObj : backup)
						{
							if (removedObj.getX() == existingObj.getX() && removedObj.getY() == existingObj.getY() && removedObj.getType() == existingObj.getType())
							{
								backup.remove(existingObj);
								break;
							}
						}
					}
					for (final MarioObject addedObj : _addedObjects)
						backup.add(addedObj);
					
					int playerAmount = 0;
					for (final MarioObject obj : backup)
						if (obj.getType() == MarioType.PLAYER)
							playerAmount++;
					if (playerAmount != 1)
						client.sendPacket(GameEditResponse.FAIL);
					else if (!MarioTable.getInstance().updateDatabase(backup))
						client.sendPacket(GameEditResponse.FAIL);
					else
					{
						client.sendPacket(GameEditResponse.SUCCESS);
						
						Broadcast.toAllUsers(new GameObjectsResponse(GameId.MARIO));
					}
					break;
				case PACMAN:
					boolean hasPlayer = false;
					boolean hasAtLeastOneStar = false;
					for (int i = 0;i < _objects.length;i++)
					{
						for (int j = 0;j < _objects[i].length;j++)
						{
							if (_objects[i][j].isPlayer())
								hasPlayer = true;
							else if (_objects[i][j].isStar())
								hasAtLeastOneStar = true;
						}
					}
					
					if (!hasPlayer || !hasAtLeastOneStar)
						client.sendPacket(GameEditResponse.FAIL);
					else
					{
						PacmanTable.getInstance().updateMap(_mapId, _objects);
						
						client.sendPacket(GameEditResponse.SUCCESS);
						
						Broadcast.toAllUsers(new GameObjectsResponse(GameId.PACMAN));
					}
					break;
			}
		}
	}
}