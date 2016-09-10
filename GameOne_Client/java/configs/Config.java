package configs;

import java.awt.Color;

import util.parsers.properties.ExProperties;

/**
 * Load all client configurations files.
 * @author Sahar
 */
public final class Config
{
	// --------------------------------------------------
	// Property File Definitions
	// --------------------------------------------------
	private static final String NETWORK_FILE = "./configs/Network.properties";
	
	// --------------------------------------------------
	// Network Settings
	// --------------------------------------------------
	public static String SERVER_IP;
	public static int PORT;
	
	// --------------------------------------------------
	// Hidden
	// --------------------------------------------------
	public static final Color UI_COLOR = new Color(0x7B, 0xD9, 0xF1);
	
	public static void load()
	{
		// Load Network.properties file (if exists)
		final ExProperties network = new ExProperties(NETWORK_FILE);
		
		SERVER_IP = network.getProperty("EnableUPnP", "127.0.0.1");
		PORT = network.getProperty("Port", 777);
	}
}