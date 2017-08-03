package util.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PropertiesParser
{
	private static final Logger LOGGER = Logger.getLogger(PropertiesParser.class.getName());
	
	private final Properties _properties = new Properties();
	private final File _file;
	
	public PropertiesParser(final String fileName)
	{
		this(new File(fileName));
	}
	
	public PropertiesParser(final File file)
	{
		_file = file;
		
		try (final InputStream is = new FileInputStream(file))
		{
			_properties.load(is);
		}
		catch (final IOException e)
		{
			LOGGER.log(Level.WARNING, "Error loading config file: " + _file.getName() + " ", e);
		}
	}
	
	private String getValue(final String key)
	{
		final String value = _properties.getProperty(key);
		return value != null ? value.trim() : null;
	}
	
	public int getProperty(final String key, final int defaultValue)
	{
		return getProperty(key, defaultValue, Integer.MIN_VALUE);
	}
	
	public int getProperty(final String key, final int defaultValue, final int minimumValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warning(_file.getName() + ": missing property for key: " + key + ", using default value: " + defaultValue + ".");
			return defaultValue;
		}
		
		try
		{
			final int intValue = Integer.parseInt(value);
			if (intValue < minimumValue)
			{
				LOGGER.warning(_file.getName() + ": invalid value specified for key: " + key + ", specified default: " + value + " cannot be smaller than " + minimumValue + ", using default value: " + defaultValue + ".");
				return defaultValue;
			}
			
			return intValue;
		}
		catch (final NumberFormatException e)
		{
			LOGGER.warning(_file.getName() + ": invalid value specified for key: " + key + ", specified default: " + value + " should be \"int\", using default value: " + defaultValue + ".");
			return defaultValue;
		}
	}
	
	public boolean getProperty(final String key, final boolean defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warning(_file.getName() + ": missing property for key: " + key + ", using default value: " + defaultValue + ".");
			return defaultValue;
		}
		
		if (value.equalsIgnoreCase("true"))
			return true;
		if (value.equalsIgnoreCase("false"))
			return false;
		
		LOGGER.warning(_file.getName() + ": invalid value specified for key: " + key + ", specified default: " + value + " should be \"boolean\", using default value: " + defaultValue + ".");
		return defaultValue;
	}
	
	public String getProperty(final String key, final String defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warning(_file.getName() + ": missing property for key: " + key + ", using default value: " + defaultValue + ".");
			return defaultValue;
		}
		
		return value;
	}
}