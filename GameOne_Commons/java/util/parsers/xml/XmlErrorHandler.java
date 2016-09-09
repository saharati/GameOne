package util.parsers.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * XML error handler.
 * @author Sahar
 */
public final class XmlErrorHandler implements ErrorHandler
{
	@Override
	public void warning(final SAXParseException e) throws SAXParseException
	{
		throw e;
	}
	
	@Override
	public void error(final SAXParseException e) throws SAXParseException
	{
		throw e;
	}
	
	@Override
	public void fatalError(final SAXParseException e) throws SAXParseException
	{
		throw e;
	}
}