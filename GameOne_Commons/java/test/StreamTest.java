package test;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class is not a part of the project.
 * It is used in order to deeper understand the use of streams, as used in IPConfig.
 * @author Sahar
 */
public final class StreamTest
{
	public static void main(final String[] args)
	{
		// Create an IntStream with numbers from 1 to subnetMaskPrefix (inclusive).
		final int subnetMaskPrefix = 8;
		final IntStream intStream = IntStream.rangeClosed(1, subnetMaskPrefix);
		// r = result, e = element.
		// In the below example (optionalIntTest) we always add current element to result.
		// final OptionalInt optionalIntTest = intStream.reduce((r, e) -> r + e);
		// In the below example we do not use element, only result, we shift it left once every time and add 1 to it.
		final OptionalInt optionalInt = intStream.reduce((r, e) -> (r << 1) + 1);
		// Stack trace for the above operation (8 iterations in case of IntStream.rangeClosed(1, 8)):
		// (00000000 << 1) + 1 = 1 (00000001)
		// (00000001 << 1) + 1 = 3 (00000011)
		// (00000011 << 1) + 1 = 7 (00000111)
		// (00000111 << 1) + 1 = 15 (00001111)
		// (00001111 << 1) + 1 = 31 (00011111)
		// (00011111 << 1) + 1 = 63 (00111111)
		// (00111111 << 1) + 1 = 127 (01111111)
		// (01111111 << 1) + 1 = 255 (11111111)
		// Stack trace without the +1 part ((r, e) -> (r << 1))
		// 00000000 << 1 = 1 (00000001)
		// 00000001 << 1 = 2 (00000010)
		// 00000010 << 1 = 4 (00000100)
		// 00000100 << 1 = 8 (00001000)
		// 00001000 << 1 = 16 (00010000)
		// 00010000 << 1 = 32 (00100000)
		// 00100000 << 1 = 64 (01000000)
		// 01000000 << 1 = 128 (10000000)
		final int result = optionalInt.orElse(0);
		// To conclude, for 8 bits network mask (255.0.0.0) its 255.
		// For 16 bits network mask (255.255.0.0) its 65535.
		// For 24 bits network mask (255.255.255.0) its 16777215.
		final int hostAddressInt = result << (32 - subnetMaskPrefix);
		// After reversing, the result would be:
		// For 8 bits network mask (255.0.0.0) its -16777216 (negative because of integer overflow 11111111000000000000000000000000).
		// For 16 bits network mask (255.255.0.0) its -65536 (11111111111111110000000000000000).
		// For 24 bits network mask (255.255.255.0) its -256 (11111111111111111111111100000000).
		System.out.println("===== HOST ADDRESS INT =====");
		System.out.println(hostAddressInt);
		System.out.println(Integer.toBinaryString(hostAddressInt));
		
		// ========================================
		// Part 2.
		// ========================================
		
		final String someIP = "127.0.0.1";
		final String[] ipSplit = someIP.split("\\.");
		final Stream<String> ipStringStream = Arrays.stream(ipSplit);
		final IntStream ipIntStream = ipStringStream.mapToInt(Integer::parseInt);
		// Shift left the result 8 times and then add the current element to the result.
		final OptionalInt ipToIntRepresentation = ipIntStream.reduce((r, e) -> (r << 8) + e);
		// Stack trace for the above:
		// (00000000 << 8) + 127 = 127 (01111111)
		// (01111111 << 8) + 0 = 32512 (111111100000000)
		// (111111100000000 << 8) + 0 = 8323072 (11111110000000000000000)
		// (11111110000000000000000 << 8) + 1 = 2130706433 (1111111000000000000000000000001)
		final int subnetMaskInt = ipToIntRepresentation.orElse(0);
		System.out.println("===== SUBNET MASK INT =====");
		System.out.println(subnetMaskInt);
		// 11111111000000000000000000000000
		// &
		// 01111111000000000000000000000001
		// =
		// 01111111000000000000000000000000 (2130706432)
		final int subnetAddressInt = hostAddressInt & subnetMaskInt;
		System.out.println("===== SUBNET ADDRESS INT =====");
		System.out.println(subnetAddressInt);
		System.out.println(Integer.toBinaryString(subnetAddressInt));
		
		// ========================================
		// Part 3.
		// ========================================
		
		// 0xF = 1111, 0xFF = 11111111 and so on.
		final int firstOctat = (subnetAddressInt >> 24) & 0xFF; // 255
		final int secondOctat = (subnetAddressInt >> 16) & 0xFF; // 0
		final int thirdOctat = (subnetAddressInt >> 8) & 0xFF; // 0
		final int fourthOctat = subnetAddressInt & 0xFF; // 0
		final String subnetAddress = firstOctat + "." + secondOctat + "." + thirdOctat + "." + fourthOctat;
		final String subnet = subnetAddress + '/' + subnetMaskPrefix;
		System.out.println("===== SUBNET ADDRESS =====");
		System.out.println(subnet);
	}
}