package util.parsers;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class IXmlReader implements FileFilter
{
	public abstract void load();
	
	public abstract void parseDocument(final Document doc, final File f);
	
	protected final void parseFile(final File f) throws SAXException, IOException
	{
		if (!accept(f))
			throw new IllegalArgumentException("File " + f.getName() + " is not an XML file!");
		
		parseDocument(XmlFactory.BUILDER.parse(f), f);
	}
	
	protected final void parseDirectory(final File dir, final boolean recursive) throws SAXException, IOException
	{
		if (!dir.exists() || !dir.isDirectory())
			throw new IllegalArgumentException("Directory " + dir.getName() + " doesn't exist or is not a folder!");
		
		final File[] listOfFiles = dir.listFiles();
		for (final File f : listOfFiles)
		{
			if (recursive && f.isDirectory())
				parseDirectory(f, recursive);
			else if (accept(f))
				parseFile(f);
		}
	}
	
	protected static final int parseInt(final Node node) throws NumberFormatException
	{
		final String value = node.getNodeValue();
		if (value == null)
			throw new NullPointerException(node.getNodeName() + " doesn't have a value assigned.");
		
		return Integer.parseInt(value);
	}
	
	protected static final long parseLong(final Node node) throws NumberFormatException
	{
		final String value = node.getNodeValue();
		if (value == null)
			throw new NullPointerException(node.getNodeName() + " doesn't have a value assigned.");
		
		return Long.parseLong(value);
	}
	
	protected static final double parseDouble(final Node node) throws NumberFormatException
	{
		final String value = node.getNodeValue();
		if (value == null)
			throw new NullPointerException(node.getNodeName() + " doesn't have a value assigned.");
		
		return Double.parseDouble(value);
	}
	
	protected static final boolean parseBoolean(final Node node)
	{
		final String value = node.getNodeValue();
		if (value == null)
			throw new NullPointerException(node.getNodeName() + " doesn't have a value assigned.");
		
		if (value.equalsIgnoreCase("true"))
			return true;
		if (value.equalsIgnoreCase("false"))
			return false;
		
		throw new IllegalArgumentException(node.getNodeName() + ": boolean value required but found: " + value);
	}
	
	protected static final String parseString(final Node node)
	{
		final String value = node.getNodeValue();
		if (value == null)
			throw new NullPointerException(node.getNodeName() + " doesn't have a value assigned.");
		
		return value;
	}
	
	protected static final Map<String, Object> parseAttributes(final Node node)
	{
		final NamedNodeMap attrs = node.getAttributes();
		final Map<String, Object> map = new HashMap<>();
		for (int i = 0;i < attrs.getLength();i++)
		{
			final Node att = attrs.item(i);
			map.put(att.getNodeName(), att.getNodeValue());
		}
		
		return map;
	}
	
	protected static final void forEach(final Node node, final Consumer<Node> action)
	{
		forEach(node, a -> true, action);
	}
	
	protected static final void forEach(final Node node, final String nodeName, final Consumer<Node> action)
	{
		forEach(node, innerNode -> nodeName.equalsIgnoreCase(innerNode.getNodeName()), action);
	}
	
	protected static final void forEach(final Node node, final Predicate<Node> filter, final Consumer<Node> action)
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
	public final boolean accept(final File f)
	{
		if (f == null || !f.isFile())
			return false;
		
		return f.getName().toLowerCase().endsWith(".xml");
	}
}