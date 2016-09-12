package network;

import java.nio.ByteBuffer;

/**
 * A nicer version of ByteBuffer.
 * @author Sahar
 */
public final class PacketReader
{
	private final ByteBuffer _buf = ByteBuffer.allocateDirect(1024);
	
	public ByteBuffer getBuffer()
	{
		return _buf;
	}
	
	public int getRemainingBytes()
	{
		return _buf.remaining();
	}
	
	public byte readByte()
	{
		return _buf.get();
	}
	
	public short readShort()
	{
		return _buf.getShort();
	}
	
	public int readInt()
	{
		return _buf.getInt();
	}
	
	public long readLong()
	{
		return _buf.getLong();
	}
	
	public float readFloat()
	{
		return _buf.getFloat();
	}
	
	public double readDouble()
	{
		return _buf.getDouble();
	}
	
	public char readChar()
	{
		return _buf.getChar();
	}
	
	public boolean readBoolean()
	{
		return _buf.get() == 1;
	}
	
	public String readString()
	{
		final StringBuilder sb = new StringBuilder();
		char chr;
		while ((chr = _buf.getChar()) != '\000')
			sb.append(chr);
		
		return sb.toString();
	}
	
	public byte[] readBytes(final int length)
	{
		final byte[] result = new byte[length];
		_buf.get(result);
		return result;
	}
}