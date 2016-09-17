package network;

import java.nio.ByteBuffer;

/**
 * A nicer version of ByteBuffer.
 * @author Sahar
 */
public abstract class PacketReader<T extends BasicClient>
{
	private ByteBuffer _buf;
	
	public abstract void read(final T client);
	
	public abstract void run(final T client);
	
	public void setBuffer(final ByteBuffer buf)
	{
		_buf = buf;
	}
	
	public ByteBuffer getBuffer()
	{
		return _buf;
	}
	
	public int getRemainingBytes()
	{
		return _buf.remaining();
	}
	
	protected byte readByte()
	{
		return _buf.get();
	}
	
	protected short readShort()
	{
		return _buf.getShort();
	}
	
	protected int readInt()
	{
		return _buf.getInt();
	}
	
	protected long readLong()
	{
		return _buf.getLong();
	}
	
	protected float readFloat()
	{
		return _buf.getFloat();
	}
	
	protected double readDouble()
	{
		return _buf.getDouble();
	}
	
	protected char readChar()
	{
		return _buf.getChar();
	}
	
	protected boolean readBoolean()
	{
		return _buf.get() == 1;
	}
	
	protected String readString()
	{
		final StringBuilder sb = new StringBuilder();
		char chr;
		while ((chr = _buf.getChar()) != '\000')
			sb.append(chr);
		
		return sb.toString();
	}
	
	protected byte[] readBytes(final int length)
	{
		final byte[] result = new byte[length];
		_buf.get(result);
		return result;
	}
}