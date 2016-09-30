package network.response;

import network.PacketInfo;
import network.PacketWriter;

/**
 * Writing a response for a duel invitation.
 * @author Sahar
 */
public final class DuelInviteResponse extends PacketWriter
{
	public static final DuelInviteResponse CANNOT_INVITE_SELF = new DuelInviteResponse((byte) -1);
	public static final DuelInviteResponse TARGET_UNAVAILABLE = new DuelInviteResponse((byte) -2);
	public static final DuelInviteResponse CANCELLED = new DuelInviteResponse((byte) -3);
	public static final DuelInviteResponse WAIT = new DuelInviteResponse((byte) 1);
	public static final byte RESPOND = (byte) 2;
	
	private final byte _result;
	private final String _username;
	
	private DuelInviteResponse(final byte result)
	{
		this(result, null);
	}
	
	public DuelInviteResponse(final byte result, final String username)
	{
		_result = result;
		_username = username;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.INVITE.ordinal());
		
		writeByte(_result);
		writeString(_username);
	}
}