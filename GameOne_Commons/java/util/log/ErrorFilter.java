package util.log;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Filter for error logs.
 * @author Sahar
 */
public final class ErrorFilter implements Filter
{
	@Override
	public boolean isLoggable(final LogRecord record)
	{
		return record.getThrown() != null;
	}
}