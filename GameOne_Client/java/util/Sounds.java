package util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import util.threadpool.ThreadPool;

/**
 * Utility used to create sounds.
 * @author Sahar
 */
public final class Sounds
{
	private static final Logger LOGGER = Logger.getLogger(Sounds.class.getName());
	
	public static void playSound(final int hz, final int msecs) 
	{
		playSound(hz, msecs, 1.0);
	}
	
	public static void playSound(final int hz, final int msecs, final double vol)
	{
		ThreadPool.execute(() ->
		{
			try
			{
				final byte[] buf = new byte[1];
				final AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
				try (final SourceDataLine sdl = AudioSystem.getSourceDataLine(af))
				{
					sdl.open(af);
					sdl.start();
					
					for (int i = 0;i < msecs * 8;i++)
					{
						final double angle = i / (8000f / hz) * 2.0 * Math.PI;
						
						buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
						sdl.write(buf, 0, 1);
					}
					
					sdl.drain();
					sdl.stop();
				}
			}
			catch (final LineUnavailableException e)
			{
				LOGGER.log(Level.WARNING, "Unable to play sound: ", e);
			}
		});
	}
}