package util.log;

import java.io.IOException;
import java.util.logging.FileHandler;

public final class ErrorLogHandler extends FileHandler
{
	public ErrorLogHandler() throws IOException, SecurityException
	{
		super();
	}
}