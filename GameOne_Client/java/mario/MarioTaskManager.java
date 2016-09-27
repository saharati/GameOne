package mario;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import mario.objects.AbstractObject;
import util.threadpool.ThreadPool;

/**
 * This class manages all animations.
 * @author Sahar
 */
public final class MarioTaskManager extends CopyOnWriteArrayList<AbstractObject> implements Runnable
{
	private static final long serialVersionUID = 8406133696365848464L;
	
	private ScheduledFuture<?> _future;
	
	private MarioTaskManager()
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
		for (final AbstractObject o : this)
			o.notifyTimeOut();
	}
	
	public static final MarioTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final MarioTaskManager INSTANCE = new MarioTaskManager();
	}
}