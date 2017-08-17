package network.request;

import network.PacketInfo;
import network.PacketWriter;
import objects.GameResult;

public final class RequestUpdateGameScore extends PacketWriter
{
	private final GameResult _result;
	private final int _totalScore;
	
	public RequestUpdateGameScore(final GameResult result, final int totalScore)
	{
		_result = result;
		_totalScore = totalScore;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.SCORE.ordinal());
		
		writeInt(_result.ordinal());
		writeInt(_totalScore);
	}
}