package util.random;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Extended random class.
 * @author Sahar
 */
public final class Rnd
{
	public static double nextDouble()
	{
		return ThreadLocalRandom.current().nextDouble();
	}
	
	public static int nextInt()
	{
		return ThreadLocalRandom.current().nextInt();
	}
	
	public static long nextLong()
	{
		return ThreadLocalRandom.current().nextLong();
	}
	
	public static double nextGaussian()
	{
		return ThreadLocalRandom.current().nextGaussian();
	}
	
	public static boolean nextBoolean()
	{
		return ThreadLocalRandom.current().nextBoolean();
	}
	
	public static int get(final int n)
	{
		return ThreadLocalRandom.current().nextInt(n);
	}
	
	public static int get(final int min, final int max)
	{
		return ThreadLocalRandom.current().nextInt(min, max == Integer.MAX_VALUE ? max : max + 1);
	}
	
	public static long get(final long n)
	{
		return ThreadLocalRandom.current().nextLong(n);
	}
	
	public static long get(final long min, final long max)
	{
		return ThreadLocalRandom.current().nextLong(min, max == Long.MAX_VALUE ? max : max + 1L);
	}
	
	public static boolean calcChance(final int applicableUnits, final int totalUnits)
	{
		return applicableUnits > get(totalUnits);
	}
	
	public static byte[] nextBytes(final int count)
	{
		return nextBytes(new byte[count]);
	}
	
	public static byte[] nextBytes(final byte[] array)
	{
		ThreadLocalRandom.current().nextBytes(array);
		return array;
	}
	
	public static <T> T get(final List<T> list)
	{
		if (list == null || list.isEmpty())
			return null;
		
		return list.get(get(list.size()));
	}
	
	public static <T> T get(final T[] array)
	{
		if (array == null || array.length == 0)
			return null;
		
		return array[get(array.length)];
	}
}