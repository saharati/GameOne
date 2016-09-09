package util.parsers.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * .properties files parser.
 * @author Sahar
 */
public final class ExProperties extends Properties
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(ExProperties.class.getName());
	private static final String DEFAULT_DELIMITER = "[\\s,;]+";
	
	public ExProperties(final String fileName)
	{
		this(new File(fileName));
	}
	
	public ExProperties(final File file)
	{
		try (final InputStream is = new FileInputStream(file))
		{
			load(is);
		}
		catch (final IOException e)
		{
			LOGGER.log(Level.WARNING, "Error loading \"" + file.getName() + "\" config: ", e);
		}
	}
	
	public boolean getProperty(final String name, final boolean defaultValue)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
			return Boolean.parseBoolean(value);
		
		return defaultValue;
	}
	
	public int getProperty(final String name, final int defaultValue)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
			return Integer.parseInt(value);
		
		return defaultValue;
	}
	
	public long getProperty(final String name, final long defaultValue)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
			return Long.parseLong(value);
		
		return defaultValue;
	}
	
	public double getProperty(final String name, final double defaultValue)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
			return Double.parseDouble(value);
		
		return defaultValue;
	}
	
	public String[] getProperty(final String name, final String[] defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public String[] getProperty(final String name, final String[] defaultValue, final String delimiter)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
			return value.split(delimiter);
		
		return defaultValue;
	}
	
	public boolean[] getProperty(final String name, final boolean[] defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public boolean[] getProperty(final String name, final boolean[] defaultValue, final String delimiter)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
		{
			final String[] values = value.split(delimiter);
			final boolean[] val = new boolean[values.length];
			for (int i = 0;i < val.length;i++)
				val[i] = Boolean.parseBoolean(values[i]);
			
			return val;
		}
		
		return defaultValue;
	}
	
	public int[] getProperty(final String name, final int[] defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public int[] getProperty(final String name, final int[] defaultValue, final String delimiter)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
		{
			final String[] values = value.split(delimiter);
			final int[] val = new int[values.length];
			for (int i = 0;i < val.length;i++)
				val[i] = Integer.parseInt(values[i]);
			
			return val;
		}
		
		return defaultValue;
	}
	
	public long[] getProperty(final String name, final long[] defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public long[] getProperty(final String name, final long[] defaultValue, final String delimiter)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
		{
			final String[] values = value.split(delimiter);
			final long[] val = new long[values.length];
			for (int i = 0;i < val.length;i++)
				val[i] = Long.parseLong(values[i]);
			
			return val;
		}
		
		return defaultValue;
	}
	
	public double[] getProperty(final String name, final double[] defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public double[] getProperty(final String name, final double[] defaultValue, final String delimiter)
	{
		final String value = super.getProperty(name, null);
		if (value != null)
		{
			final String[] values = value.split(delimiter);
			final double[] val = new double[values.length];
			for (int i = 0;i < val.length;i++)
				val[i] = Double.parseDouble(values[i]);
			
			return val;
		}
		
		return defaultValue;
	}
}