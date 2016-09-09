package util.parsers.xml;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Interface for XML parsers.
 * @author Sahar
 */
public interface IXmlReader extends FileFilter
{
	Logger LOGGER = Logger.getLogger(IXmlReader.class.getName());
	
	/**
	 * This method can be used to load/reload the data.<br>
	 * It's highly recommended to clear the data storage, either the list or map.
	 */
	void load();
	
	/**
	 * Abstract method that when implemented will parse the current document.<br>
	 * Is expected to be called from {@link #parseFile(File)}.
	 * @param doc the current document to parse
	 * @param f the current file
	 */
	void parseDocument(final Document doc, final File f);
	
	default void parseFile(final File f)
	{
		if (!accept(f))
		{
			LOGGER.warning("Could not parse " + f.getName() + " is not XML file!");
			return;
		}
		
		try
		{
			parseDocument(XmlFactory.BUILDER.parse(f), f);
		}
		catch (final SAXException | IOException e)
		{
			LOGGER.log(Level.WARNING, "Could not parse file: " + f.getName() + " : ", e);
		}
	}
	
	default boolean parseDirectory(final File dir, final boolean recursive)
	{
		if (!dir.exists())
		{
			LOGGER.warning("Folder " + dir.getAbsolutePath() + " doesn't exist!");
			return false;
		}
		
		final File[] listOfFiles = dir.listFiles();
		for (final File f : listOfFiles)
		{
			if (recursive && f.isDirectory())
				parseDirectory(f, recursive);
			else if (accept(f))
				parseFile(f);
		}
		
		return true;
	}
	
	default Boolean parseBoolean(final Node node, final Boolean defaultValue)
	{
		return node != null ? Boolean.valueOf(node.getNodeValue()) : defaultValue;
	}
	
	default Byte parseByte(final Node node, final Byte defaultValue)
	{
		return node != null ? Byte.decode(node.getNodeValue()) : defaultValue;
	}
	
	default Short parseShort(final Node node, final Short defaultValue)
	{
		return node != null ? Short.decode(node.getNodeValue()) : defaultValue;
	}
	
	default int parseInt(final Node node, final Integer defaultValue)
	{
		return node != null ? Integer.decode(node.getNodeValue()) : defaultValue;
	}
	
	default Long parseLong(final Node node, final Long defaultValue)
	{
		return node != null ? Long.decode(node.getNodeValue()) : defaultValue;
	}
	
	default Float parseFloat(final Node node, final Float defaultValue)
	{
		return node != null ? Float.valueOf(node.getNodeValue()) : defaultValue;
	}
	
	default Double parseDouble(final Node node, final Double defaultValue)
	{
		return node != null ? Double.valueOf(node.getNodeValue()) : defaultValue;
	}
	
	default String parseString(final Node node, final String defaultValue)
	{
		return node != null ? node.getNodeValue() : defaultValue;
	}
	
	default <T extends Enum<T>> T parseEnum(final Node node, final Class<T> clazz, final T defaultValue)
	{
		if (node == null)
			return defaultValue;
		
		try
		{
			return Enum.valueOf(clazz, node.getNodeValue());
		}
		catch (final IllegalArgumentException e)
		{
			LOGGER.warning("Invalid value specified for node: " + node.getNodeName() + " specified value: " + node.getNodeValue() + " should be enum value of \"" + clazz.getSimpleName() + "\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	default Map<String, Object> parseAttributes(final Node node)
	{
		final NamedNodeMap attrs = node.getAttributes();
		final Map<String, Object> map = new LinkedHashMap<>();
		for (int i = 0;i < attrs.getLength();i++)
		{
			final Node att = attrs.item(i);
			map.put(att.getNodeName(), att.getNodeValue());
		}
		
		return map;
	}
	
	default void forEach(final Node node, final Consumer<Node> action)
	{
		forEach(node, a -> true, action);
	}
	
	default void forEach(final Node node, final String nodeName, final Consumer<Node> action)
	{
		forEach(node, innerNode -> nodeName.equalsIgnoreCase(innerNode.getNodeName()), action);
	}
	
	default void forEach(final Node node, final Predicate<Node> filter, final Consumer<Node> action)
	{
		final NodeList list = node.getChildNodes();
		for (int i = 0;i < list.getLength();i++)
		{
			final Node targetNode = list.item(i);
			if (filter.test(targetNode))
				action.accept(targetNode);
		}
	}
	
	@Override
	default boolean accept(final File f)
	{
		if (f == null || !f.isFile())
			return false;
		
		return f.getName().toLowerCase().endsWith(".xml");
	}
}