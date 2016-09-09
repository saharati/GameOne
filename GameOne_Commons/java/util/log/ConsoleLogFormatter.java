package util.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for console logs.
 * @author Sahar
 */
public final class ConsoleLogFormatter extends Formatter
{
	private static final String CRLF = "\r\n";
	
	@Override
	public String format(final LogRecord record)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(record.getMessage());
		sb.append(CRLF);
		
		if (record.getThrown() != null)
		{
			sb.append(record.getThrown().getMessage());
			sb.append(CRLF);
		}
		
		return sb.toString();
	}
}