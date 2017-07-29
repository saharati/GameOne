package util.random;

import java.util.concurrent.ThreadLocalRandom;

public final class Rnd
{
	/**
	 * Get a random boolean.
	 * @return {@code true} or {@code false}, randomly.
	 */
	public static boolean nextBoolean()
	{
		return ThreadLocalRandom.current().nextBoolean();
	}
	
	/**
	 * Get a random integer between 0 (inclusive) to n (exclusive).
	 * @param n - The upper bound, exclusive.
	 * @return {@code int} | 0 <= {@code int} < n
	 */
	public static int get(final int n)
	{
		return ThreadLocalRandom.current().nextInt(n);
	}
	
	/**
	 * Get a random integer between min (inclusive) and max (inclusive).
	 * @param min - The lower bound, inclusive.
	 * @param max - The upper bound, inclusive.
	 * @return {@code int} | min <= {@code int} <= max
	 */
	public static int get(final int min, final int max)
	{
		return ThreadLocalRandom.current().nextInt(min, max == Integer.MAX_VALUE ? max : max + 1);
	}
}