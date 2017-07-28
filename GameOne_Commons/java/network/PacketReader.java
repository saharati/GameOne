package network;

import java.nio.ByteBuffer;

public abstract class PacketReader<T extends BasicClient>
{
	private ByteBuffer _buf;
	
	public abstract void read();
	
	public abstract void run(final T client);
	
	public final void setBuffer(final ByteBuffer buf)
	{
		_buf = buf;
	}
	
	protected byte readByte()
	{
		return _buf.get();
	}
	
	protected int readInt()
	{
		return _buf.getInt();
	}
	
	protected long readLong()
	{
		return _buf.getLong();
	}
	
	protected double readDouble()
	{
		return _buf.getDouble();
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
}