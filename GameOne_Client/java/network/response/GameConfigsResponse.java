package network.response;

import java.util.HashMap;
import java.util.Map;

import client.Client;
import configs.GameConfig;
import network.PacketReader;

/**
 * Packet responsible for feeding up game configs.
 * @author Sahar
 */
public final class GameConfigsResponse extends PacketReader<Client>
{
	private Map<String, String> _configs;
	
	@Override
	public void read()
	{
		final int size = readInt();
		
		_configs = new HashMap<>(size);
		for (int i = 0;i < size;i++)
			_configs.put(readString(), readString());
	}
	
	@Override
	public void run(final Client client)
	{
		GameConfig.load(_configs);
	}
}