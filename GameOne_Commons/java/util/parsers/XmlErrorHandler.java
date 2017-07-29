package util.parsers;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

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