package util.log;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public final class ErrorFilter implements Filter
{
	@Override
	public boolean isLoggable(final LogRecord record)
	{
		return record.getThrown() != null;
	}
}