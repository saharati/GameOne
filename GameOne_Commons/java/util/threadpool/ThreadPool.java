package util.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import util.configs.CommonConfig;

public final class ThreadPool
{
	private static final Logger LOGGER = Logger.getLogger(ThreadPool.class.getName());
	
	private static ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR;
	private static ThreadPoolExecutor INSTANT_THREAD_POOL_EXECUTOR;
	
	public static void load()
	{
		final int scheduledThreadPoolSize = CommonConfig.SCHEDULED_THREAD_POOL_SIZE < 1 ? Runtime.getRuntime().availableProcessors() * 4 : CommonConfig.SCHEDULED_THREAD_POOL_SIZE;
		final int instantThreadPoolSize = CommonConfig.INSTANT_THREAD_POOL_SIZE < 1 ? Runtime.getRuntime().availableProcessors() * 2 : CommonConfig.INSTANT_THREAD_POOL_SIZE;
		
		SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(scheduledThreadPoolSize);
		INSTANT_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(instantThreadPoolSize, instantThreadPoolSize, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
		
		SCHEDULED_THREAD_POOL_EXECUTOR.prestartAllCoreThreads();
		INSTANT_THREAD_POOL_EXECUTOR.prestartAllCoreThreads();
		
		scheduleAtFixedRate(() ->
		{
			SCHEDULED_THREAD_POOL_EXECUTOR.purge();
			INSTANT_THREAD_POOL_EXECUTOR.purge();
		}, 1, 1, TimeUnit.MINUTES);
		
		LOGGER.info("Initialized " + scheduledThreadPoolSize + " scheduled, " + instantThreadPoolSize + " instant thread(s).");
	}
	
	public static ScheduledFuture<?> schedule(final Runnable r, final long delay, final TimeUnit timeUnit)
	{
		return SCHEDULED_THREAD_POOL_EXECUTOR.schedule(r, delay, timeUnit);
	}
	
	public static ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long delay, final long period, final TimeUnit timeUnit)
	{
		return SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(r, delay, period, timeUnit);
	}
	
	public static void execute(final Runnable r)
	{
		INSTANT_THREAD_POOL_EXECUTOR.execute(r);
	}
	
	public static void shutdown()
	{
		SCHEDULED_THREAD_POOL_EXECUTOR.shutdownNow();
		INSTANT_THREAD_POOL_EXECUTOR.shutdownNow();
	}
}