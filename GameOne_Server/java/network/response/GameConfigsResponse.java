package network.response;

import java.util.Map.Entry;

import network.PacketInfo;
import network.PacketWriter;
import util.configs.GameConfig;

public final class GameConfigsResponse extends PacketWriter
{
	@Override
	public void write()
	{
		writeInt(PacketInfo.CONFIGS.ordinal());
		
		writeInt(GameConfig.CONFIGS.size());
		for (final Entry<String, String> e : GameConfig.CONFIGS.entrySet())
		{
			writeString(e.getKey());
			writeString(e.getValue());
		}
	}
}