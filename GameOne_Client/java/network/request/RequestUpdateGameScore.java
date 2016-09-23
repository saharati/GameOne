package network.request;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Packing updating server with new score data from current game.
 * @author Sahar
 */
public final class RequestUpdateGameScore extends PacketWriter
{
	private final boolean _isWin;
	private final int _totalScore;
	
	public RequestUpdateGameScore(final boolean isWin, final int totalScore)
	{
		_isWin = isWin;
		_totalScore = totalScore;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.SCORE.ordinal());
		
		writeBoolean(_isWin);
		writeInt(_totalScore);
	}
}