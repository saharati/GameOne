package server.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

import util.configs.Config;

/**
 * Automatically open ports on a router.
 * @author Sahar
 */
public final class UPnPService
{
	private static final Logger LOGGER = Logger.getLogger(UPnPService.class.getName());
	
	private static final GatewayDiscover _gatewayDiscover = new GatewayDiscover();
	private static GatewayDevice _activeGW;
	
	public static void openPorts()
	{
		if (!Config.ENABLE_UPNP)
		{
			LOGGER.info("UPnP Service is disabled.");
			return;
		}
		
		try
		{
			LOGGER.info("Looking for UPnP Gateway Devices...");
			_gatewayDiscover.discover();
			
			// Choose the first active gateway for the tests.
			_activeGW = _gatewayDiscover.getValidGateway();
			if (_activeGW == null)
				LOGGER.info("No active UPnP gateway found.");
			else
			{
				LOGGER.info("Using UPnP gateway: " + _activeGW.getFriendlyName() + ".");
				LOGGER.info("Using local address: " + _activeGW.getLocalAddress().getHostAddress() + " External address: " + _activeGW.getExternalIPAddress() + ".");
				
				final PortMappingEntry portMapping = new PortMappingEntry();
				final InetAddress localAddress = _activeGW.getLocalAddress();
				
				if (_activeGW.getSpecificPortMappingEntry(Config.PORT, "TCP", portMapping))
					LOGGER.info("Mapping already exists on [" + localAddress.getHostAddress() + ":" + Config.PORT + "]");
				else if (_activeGW.addPortMapping(Config.PORT, Config.PORT, localAddress.getHostAddress(), "TCP", "GameOne_Server"))
					LOGGER.info("Mapping successfull on [" + localAddress.getHostAddress() + ":" + Config.PORT + "]");
				else
					LOGGER.info("Mapping failed on [" + localAddress.getHostAddress() + ":" + Config.PORT + "]");
			}
		}
		catch (final IOException | SAXException | ParserConfigurationException e)
		{
			LOGGER.log(Level.WARNING, "Problem occured while attempting port mapping: ", e);
		}
	}
	
	public static void removePorts() throws IOException, SAXException
	{
		if (!Config.ENABLE_UPNP)
			return;
		
		if (_activeGW != null && _activeGW.deletePortMapping(Config.PORT, "TCP"))
			LOGGER.info("Deleted port mapping on [" + _activeGW.getLocalAddress().getHostAddress() + ":" + Config.PORT + "]");
	}
}