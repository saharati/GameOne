package network.response;

import network.PacketInfo;
import network.PacketWriter;
import objects.DuelInviteResult;

/**
 * Writing a response for a duel invitation.
 * @author Sahar
 */
public final class DuelInviteResponse extends PacketWriter
{
	public static final DuelInviteResponse CANNOT_INVITE_SELF = new DuelInviteResponse(DuelInviteResult.CANNOT_INVITE_SELF);
	public static final DuelInviteResponse TARGET_UNAVAILABLE = new DuelInviteResponse(DuelInviteResult.TARGET_UNAVAILABLE);
	public static final DuelInviteResponse CANCELLED = new DuelInviteResponse(DuelInviteResult.CANCELLED);
	public static final DuelInviteResponse WAIT = new DuelInviteResponse(DuelInviteResult.WAIT);
	
	private final DuelInviteResult _result;
	private final String _username;
	
	private DuelInviteResponse(final DuelInviteResult result)
	{
		this(result, null);
	}
	
	public DuelInviteResponse(final DuelInviteResult result, final String username)
	{
		_result = result;
		_username = username;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.INVITE.ordinal());
		
		writeInt(_result.ordinal());
		writeString(_username);
	}
}