package util.configs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Load all network configurations.
 * @author Sahar
 */
public final class IPConfig
{
	private static final Logger LOGGER = Logger.getLogger(IPConfig.class.getName());
	private static final String TEST_URL = "https://api.ipify.org/";
	
	public static final List<String> SUBNETS = new ArrayList<>();
	public static final List<String> HOSTS = new ArrayList<>();
	
	public static void load()
	{
		HOSTS.clear();
		SUBNETS.clear();
		
		String externalIp = "127.0.0.1";
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(new URL(TEST_URL).openStream())))
		{
			externalIp = in.readLine();
		}
		catch (final IOException e)
		{
			LOGGER.info("Failed to connect to " + TEST_URL + " please check your internet connection using 127.0.0.1!");
		}
		
		try
		{
			final Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
			while (niList.hasMoreElements())
			{
				final NetworkInterface ni = niList.nextElement();
				if (!ni.isUp() || ni.isVirtual())
					continue;
				if (!ni.isLoopback())
				{
					if (ni.getHardwareAddress() == null)
						continue;
					// Skip IPv6.
					if (ni.getHardwareAddress().length != 6)
						continue;
				}
				
				for (final InterfaceAddress ia : ni.getInterfaceAddresses())
				{
					// Skip IPv6.
					if (ia.getAddress() instanceof Inet6Address)
						continue;
					
					final String hostAddress = ia.getAddress().getHostAddress();
					final int subnetPrefixLength = ia.getNetworkPrefixLength();
					final int subnetMaskInt = IntStream.rangeClosed(1, subnetPrefixLength).reduce((r, e) -> (r << 1) + 1).orElse(0) << (32 - subnetPrefixLength);
					final int hostAddressInt = Arrays.stream(hostAddress.split("\\.")).mapToInt(Integer::parseInt).reduce((r, e) -> (r << 8) + e).orElse(0);
					final int subnetAddressInt = hostAddressInt & subnetMaskInt;
					final String subnetAddress = ((subnetAddressInt >> 24) & 0xFF) + "." + ((subnetAddressInt >> 16) & 0xFF) + "." + ((subnetAddressInt >> 8) & 0xFF) + "." + (subnetAddressInt & 0xFF);
					final String subnet = subnetAddress + '/' + subnetPrefixLength;
					
					if (!SUBNETS.contains(subnet) && !subnet.equals("0.0.0.0/0"))
					{
						HOSTS.add(hostAddress);
						SUBNETS.add(subnet);
						
						LOGGER.info("Adding new subnet: " + subnet + " address: " + hostAddress);
					}
				}
			}
			
			// External host and subnet
			HOSTS.add(externalIp);
			SUBNETS.add("0.0.0.0/0");
			
			LOGGER.info("Adding new subnet: 0.0.0.0/0 address: " + externalIp);
		}
		catch (final SocketException e)
		{
			LOGGER.log(Level.INFO, "Configuration failed: ", e);
			
			System.exit(0);
		}
	}
}