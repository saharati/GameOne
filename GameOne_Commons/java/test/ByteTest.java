package test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This class is not a part of the project
 * Its intention is to better learn byte streams.
 * @author Sahar
 */
public final class ByteTest
{
	public static void main(final String[] args) throws IOException
	{
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bytes);
		out.writeUTF("Something");
		out.writeLong(3);
		out.writeInt(2);
		out.writeChar('a');
		
		final ByteBuffer buf = ByteBuffer.wrap(bytes.toByteArray());
		final byte[] arr = new byte[buf.getShort()];
		buf.get(arr);
		
		System.out.println(new String(arr));
		System.out.println(buf.getLong());
		System.out.println(buf.getInt());
		System.out.println(buf.getChar());
		
		bytes.reset();
	}
}