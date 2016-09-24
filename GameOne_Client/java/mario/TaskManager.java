package mario;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import mario.objects.AbstractObject;
import mario.objects.Player;
import util.threadpool.ThreadPool;

/**
 * This class manages all animations.
 * @author Sahar
 */
public final class TaskManager extends CopyOnWriteArrayList<AbstractObject> implements Runnable
{
	private static final long serialVersionUID = 8406133696365848464L;
	
	private ScheduledFuture<?> _future;
	
	private TaskManager()
	{
		
	}
	
	public void start()
	{
		_future = ThreadPool.scheduleAtFixedRate(this, 3, 3);
	}
	
	public void stop()
	{
		clear();
		
		_future.cancel(false);
	}
	
	@Override
	public void run()
	{
		final Player player = MarioScreen.getInstance().getPlayer();
		for (final AbstractObject o : this)
			if (Math.abs(o.getX() - player.getX()) < MarioBuilder.MAX_DISTANCE)
				o.notifyTimeOut();
	}
	
	public static final TaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final TaskManager INSTANCE = new TaskManager();
	}
}