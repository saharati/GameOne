package util;

import java.util.logging.Logger;

/**
 * Heaven for string related functions.
 * @author Sahar
 */
public final class StringUtil
{
	private static final Logger LOGGER = Logger.getLogger(StringUtil.class.getName());
	
	public static void printSection(String section)
	{
		section = "=[ " + section + " ]";
		while (section.length() < 50)
			section = "-" + section;
		
		LOGGER.info(section);
	}
}