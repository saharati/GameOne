package util;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.Shutdown;
import util.configs.Config;

/**
 * Thread checking for deadlocked threads.
 * @author Sahar
 */
public final class DeadLockDetector extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(DeadLockDetector.class.getName());
	private static final ThreadMXBean TMX = ManagementFactory.getThreadMXBean();
	
	public DeadLockDetector()
	{
		super("DeadLockDetector");
	}
	
	@Override
	public void run()
	{
		boolean deadlock = false;
		while (!deadlock)
		{
			try
			{
				final long[] ids = TMX.findDeadlockedThreads();
				if (ids != null)
				{
					deadlock = true;
					
					final ThreadInfo[] tis = TMX.getThreadInfo(ids, true, true);
					final StringBuilder info = new StringBuilder();
					
					info.append("DeadLock Found!\n");
					for (final ThreadInfo ti : tis)
						info.append(ti.toString());
					for (final ThreadInfo ti : tis)
					{
						final LockInfo[] locks = ti.getLockedSynchronizers();
						final MonitorInfo[] monitors = ti.getLockedMonitors();
						if (locks.length == 0 && monitors.length == 0)
							continue;
						
						ThreadInfo dl = ti;
						info.append("Java-level deadlock:\n");
						info.append("\t");
						info.append(dl.getThreadName());
						info.append(" is waiting to lock ");
						info.append(dl.getLockInfo().toString());
						info.append(" which is held by ");
						info.append(dl.getLockOwnerName());
						info.append("\n");
						
						while ((dl = TMX.getThreadInfo(new long[] {dl.getLockOwnerId()}, true, true)[0]).getThreadId() != ti.getThreadId())
						{
							info.append("\t");
							info.append(dl.getThreadName());
							info.append(" is waiting to lock ");
							info.append(dl.getLockInfo().toString());
							info.append(" which is held by ");
							info.append(dl.getLockOwnerName());
							info.append("\n");
						}
					}
					
					LOGGER.warning(info.toString());
					
					if (Config.RESTART_ON_DEADLOCK)
					{
						Broadcast.announceToOnlinePlayers("Server has stability issues - restarting now.");
						Shutdown.getInstance().startShutdown("DeadLockDetector - Auto Restart", 30, true);
					}
				}
				
				Thread.sleep(Config.DEADLOCK_CHECK_INTERVAL);
			}
			catch (final InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "DeadLockDetector: ", e);
			}
		}
	}
}