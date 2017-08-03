package network.request;

import network.PacketReader;
import network.response.GameStartResponse;
import server.objects.GameClient;
import server.objects.User;
import util.random.Rnd;

public final class RequestGameStart extends PacketReader<GameClient>
{
	@Override
	public void read()
	{
		
	}
	
	@Override
	public void run(final GameClient client)
	{
		final User user = client.getUser();
		switch (user.getCurrentGame())
		{
			case CHESS_MP:
			case LAMA:
			case CHECKERS:
				final User other = user.getGroup().getUsersExcept(user).findFirst().get();
				final boolean userStarts = Rnd.nextBoolean();
				
				user.sendPacket(new GameStartResponse(userStarts));
				other.sendPacket(new GameStartResponse(!userStarts));
				break;
		}
	}
}