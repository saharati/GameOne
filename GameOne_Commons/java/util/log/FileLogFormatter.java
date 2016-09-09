package util.log;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for file logs.
 * @author Sahar
 */
public final class FileLogFormatter extends Formatter
{
	private static final String CRLF = "\r\n";
	private static final String SPACE = "\t";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	@Override
	public String format(final LogRecord record)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(FORMAT.format(record.getMillis()));
		sb.append(SPACE);
		sb.append(record.getLevel().getName());
		sb.append(SPACE);
		sb.append(record.getThreadID());
		sb.append(SPACE);
		sb.append(record.getLoggerName());
		sb.append(SPACE);
		sb.append(record.getMessage());
		sb.append(CRLF);
		
		return sb.toString();
	}
}