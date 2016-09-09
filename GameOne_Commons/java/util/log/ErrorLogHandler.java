package util.log;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * Handler for error logs.
 * @author Sahar
 */
public final class ErrorLogHandler extends FileHandler
{
	public ErrorLogHandler() throws IOException, SecurityException
	{
		super();
	}
}