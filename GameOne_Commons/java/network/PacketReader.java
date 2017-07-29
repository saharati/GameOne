package network;

import java.nio.ByteBuffer;

public abstract class PacketReader<T extends BasicClient>
{
	private ByteBuffer _buffer;
	
	public abstract void read();
	
	public abstract void run(final T client);
	
	public final void setBuffer(final ByteBuffer buffer)
	{
		_buffer = buffer;
	}
	
	protected final int readInt()
	{
		return _buffer.getInt();
	}
	
	protected final long readLong()
	{
		return _buffer.getLong();
	}
	
	protected final double readDouble()
	{
		return _buffer.getDouble();
	}
	
	protected final boolean readBoolean()
	{
		return _buffer.get() == 1;
	}
	
	protected final String readString()
	{
		final StringBuilder sb = new StringBuilder();
		char chr;
		while ((chr = _buffer.getChar()) != '\000')
			sb.append(chr);
		
		return sb.toString();
	}
}