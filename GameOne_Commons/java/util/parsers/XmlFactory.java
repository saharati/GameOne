package util.parsers;

import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class XmlFactory
{
	private static final Logger LOGGER = Logger.getLogger(XmlFactory.class.getName());
	
	public static DocumentBuilder BUILDER;
	
	public static void load() throws ParserConfigurationException
	{
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(true);
		dbf.setIgnoringComments(true);
		dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
		
		BUILDER = dbf.newDocumentBuilder();
		BUILDER.setErrorHandler(new XmlErrorHandler());
		
		LOGGER.info("XmlFactory loaded!");
	}
}