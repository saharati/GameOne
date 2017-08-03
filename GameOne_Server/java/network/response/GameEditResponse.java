package network.response;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameEditResult;

public final class GameEditResponse extends PacketWriter
{
	public static final GameEditResponse NO_PERMISSION = new GameEditResponse(GameEditResult.NO_PERMISSION);
	public static final GameEditResponse FAIL = new GameEditResponse(GameEditResult.FAIL);
	public static final GameEditResponse SUCCESS = new GameEditResponse(GameEditResult.SUCCESS);
	
	private final GameEditResult _result;
	
	private GameEditResponse(final GameEditResult result)
	{
		_result = result;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.EDIT.ordinal());
		
		writeInt(_result.ordinal());
	}
}