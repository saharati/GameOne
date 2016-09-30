package network.request;

import data.sql.UsersTable;
import network.PacketReader;
import network.response.DuelInviteResponse;
import network.response.WaitingRoomResponse;
import server.objects.GameClient;
import server.objects.User;
import server.objects.UserGroup;
import util.Broadcast;

/**
 * Packet that notifies users upon duel invitation.
 * @author Sahar
 */
public final class RequestInviteToDuel extends PacketReader<GameClient>
{
	private String _target;
	
	@Override
	public void read()
	{
		_target = readString();
	}
	
	@Override
	public void run(final GameClient client)
	{
		final User user = client.getUser();
		
		// Static packet just to update user's status.
		if (_target.isEmpty())
		{
			if (user.isInGroup())
			{
				user.getGroup().getMembers().stream().filter(u -> u != user).forEach(u ->
				{
					u.sendPacket(DuelInviteResponse.CANCELLED);
					u.setGroup(null);
				});
				
				user.setGroup(null);
			}
			else
				user.setGroup(new UserGroup(user));
		}
		// Actually invite someone, update his status temporary as well.
		else
		{
			if (_target.equals(user.getUsername()))
			{
				user.setGroup(null);
				client.sendPacket(DuelInviteResponse.CANNOT_INVITE_SELF);
			}
			else
			{
				final User target = UsersTable.getInstance().getUserByName(_target);
				if (!target.isOnline() || target.isInGroup())
				{
					user.setGroup(null);
					client.sendPacket(DuelInviteResponse.TARGET_UNAVAILABLE);
				}
				else
				{
					user.getGroup().addMember(target);
					target.setGroup(user.getGroup());
					
					client.sendPacket(DuelInviteResponse.WAIT);
					target.sendPacket(new DuelInviteResponse(DuelInviteResponse.RESPOND, user.getUsername()));
				}
			}
		}
		
		Broadcast.toAllUsersOfGame(new WaitingRoomResponse(user.getCurrentGame()), user.getCurrentGame());
	}
}