package server.network.outgoing;

import data.sql.AnnouncementsTable;
import network.PacketWriter;
import server.network.PacketInfo;
import server.objects.User;
import util.Broadcast;
import util.StringUtil;

/**
 * Outgoing Login packet implementation.
 * @author Sahar
 */
public final class LoginResponse extends PacketWriter
{
	public static final byte LOGIN_OK = 1;
	
	public static final LoginResponse LOGIN_FAILED = new LoginResponse((byte) -1, null);
	public static final LoginResponse SERVER_FULL = new LoginResponse((byte) -2, null);
	public static final LoginResponse SERVER_ERROR = new LoginResponse((byte) -3, null);
	
	private final byte _result;
	private final User _user;
	
	public LoginResponse(final byte result, final User user)
	{
		_result = result;
		_user = user;
	}
	
	@Override
	public void write()
	{
		writeInt(PacketInfo.LOGIN.ordinal());
		
		writeByte(_result);
		
		if (_result == LOGIN_OK)
		{
			final String logonMsg = StringUtil.refineBeforeSend("Server", _user.getName() + " has logged on.");
			Broadcast.toAllUsersExcept(logonMsg, _user.getClient());
			
			AnnouncementsTable.getInstance().showAnnouncements(_user.getClient());
			
			if (_user.isGM())
			{
				_user.getClient().sendPacket("Server", "You have admin priviliges.");
				_user.getClient().sendPacket("Server", "Type //list for available commands.");
			}
		}
	}
}