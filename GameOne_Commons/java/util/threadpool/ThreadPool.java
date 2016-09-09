package util.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import util.configs.CommonConfig;

/**
 * Generic thread pool manager.
 * @author Sahar
 */
public final class ThreadPool
{
	private static final Logger LOGGER = Logger.getLogger(ThreadPool.class.getName());
	
	private static int _threadPoolRandomizer;
	private static ScheduledThreadPoolExecutor[] _scheduledPools;
	private static ThreadPoolExecutor[] _instantPools;
	
	public static void load()
	{
		// Feed scheduled pool.
		int poolCount = CommonConfig.SCHEDULED_THREAD_POOL_COUNT;
		if (poolCount == -1)
			poolCount = Runtime.getRuntime().availableProcessors();
		
		_scheduledPools = new ScheduledThreadPoolExecutor[poolCount];
		for (int i = 0; i < poolCount; i++)
			_scheduledPools[i] = new ScheduledThreadPoolExecutor(CommonConfig.THREADS_PER_SCHEDULED_THREAD_POOL);
		
		// Feed instant pool.
		poolCount = CommonConfig.INSTANT_THREAD_POOL_COUNT;
		if (poolCount == -1)
			poolCount = Runtime.getRuntime().availableProcessors();
		
		_instantPools = new ThreadPoolExecutor[poolCount];
		for (int i = 0; i < poolCount; i++)
			_instantPools[i] = new ThreadPoolExecutor(CommonConfig.THREADS_PER_INSTANT_THREAD_POOL, CommonConfig.THREADS_PER_INSTANT_THREAD_POOL, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100000));
		
		// Prestart core threads.
		for (final ScheduledThreadPoolExecutor threadPool : _scheduledPools)
			threadPool.prestartAllCoreThreads();
		
		for (final ThreadPoolExecutor threadPool : _instantPools)
			threadPool.prestartAllCoreThreads();
		
		// Launch purge task.
		scheduleAtFixedRate(() ->
		{
			for (final ScheduledThreadPoolExecutor threadPool : _scheduledPools)
				threadPool.purge();
			
			for (final ThreadPoolExecutor threadPool : _instantPools)
				threadPool.purge();
		}, 600000, 600000);
		
		LOGGER.info("Initialized " + (_scheduledPools.length * CommonConfig.THREADS_PER_SCHEDULED_THREAD_POOL) + " scheduled, " + (_instantPools.length * CommonConfig.THREADS_PER_INSTANT_THREAD_POOL) + " instant thread(s).");
	}
	
	public static ScheduledFuture<?> schedule(final Runnable r, final long delay)
	{
		return getPool(_scheduledPools).schedule(r, delay, TimeUnit.MILLISECONDS);
	}
	
	public static ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long delay, final long period)
	{
		return getPool(_scheduledPools).scheduleAtFixedRate(r, delay, period, TimeUnit.MILLISECONDS);
	}
	
	public static void execute(final Runnable r)
	{
		getPool(_instantPools).execute(r);
	}
	
	public static void shutdown()
	{
		for (final ScheduledThreadPoolExecutor threadPool : _scheduledPools)
			threadPool.shutdownNow();
		
		for (final ThreadPoolExecutor threadPool : _instantPools)
			threadPool.shutdownNow();
	}
	
	private static <T> T getPool(final T[] threadPools)
	{
		return threadPools[_threadPoolRandomizer++ % threadPools.length];
	}
}