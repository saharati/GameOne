package network.request;

import data.sql.PacmanTable;
import network.PacketReader;
import network.response.GameObjectsResponse;
import network.response.PacmanMapEditResponse;
import objects.GameId;
import objects.pacman.PacmanObject;
import server.objects.AccessLevel;
import server.objects.GameClient;
import util.Broadcast;

/**
 * RequestPacmanMapEdit packet implementation.
 * @author Sahar
 */
public final class RequestPacmanMapEdit extends PacketReader<GameClient>
{
	private int _mapId;
	private PacmanObject[][] _objects = new PacmanObject[16][12];
	
	@Override
	public void read(final GameClient client)
	{
		final PacmanObject[] values = PacmanObject.values();
		
		_mapId = readInt();
		for (int i = 0;i < _objects.length;i++)
			for (int j = 0;j < _objects[i].length;j++)
				_objects[i][j] = values[readInt()];
	}
	
	@Override
	public void run(final GameClient client)
	{
		if (client.getUser().getAccessLevel() != AccessLevel.GM)
			client.sendPacket(PacmanMapEditResponse.NO_PERMISSION);
		else
		{
			PacmanTable.getInstance().updateMap(_mapId, _objects);
			
			client.sendPacket(PacmanMapEditResponse.SUCCESS);
			
			Broadcast.toAllUsers(new GameObjectsResponse(GameId.PACMAN));
		}
	}
}