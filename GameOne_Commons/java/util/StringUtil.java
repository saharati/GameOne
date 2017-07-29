package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

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
	
	public static String refineBeforeSend(final String sender, final String text)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		sb.append(" ");
		sb.append(sender);
		sb.append(": ");
		sb.append(text);
		
		return sb.toString();
	}
}