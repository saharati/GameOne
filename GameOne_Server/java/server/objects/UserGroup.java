package server.objects;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Group of users connected together for a certain objective (i.e. duel invite, game etc)
 * @author Sahar
 */
public final class UserGroup
{
	private final User _leader;
	private final List<User> _members = new CopyOnWriteArrayList<>();
	
	public UserGroup(final User leader)
	{
		_leader = leader;
		_members.add(leader);
	}
	
	public User getLeader()
	{
		return _leader;
	}
	
	public List<User> getMembers()
	{
		return _members;
	}
	
	public void addMember(final User member)
	{
		_members.add(member);
	}
}