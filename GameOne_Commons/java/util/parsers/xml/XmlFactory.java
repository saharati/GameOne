package util.parsers.xml;

import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Manages a single instance of DocumentBuilder.
 * @author Sahar
 */
public final class XmlFactory
{
	private static final Logger LOGGER = Logger.getLogger(XmlFactory.class.getName());
	
	public static final DocumentBuilder BUILDER;
	
	static
	{
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(true);
		dbf.setIgnoringComments(true);
		dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
		
		try
		{
			BUILDER = dbf.newDocumentBuilder();
		}
		catch (final ParserConfigurationException e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static void load()
	{
		BUILDER.setErrorHandler(new XmlErrorHandler());
		
		LOGGER.info("XmlFactory initialized.");
	}
}