package network;

import java.nio.ByteBuffer;

public abstract class PacketWriter
{
	private final ByteBuffer _buffer = ByteBuffer.allocateDirect(BasicClient.PACKET_SIZE);
	
	public abstract void write();
	
	public final ByteBuffer getBuffer()
	{
		return _buffer;
	}
	
	protected final void writeInt(final int val)
	{
		_buffer.putInt(val);
	}
	
	protected final void writeLong(final long val)
	{
		_buffer.putLong(val);
	}
	
	protected final void writeDouble(final double val)
	{
		_buffer.putDouble(val);
	}
	
	protected final void writeBoolean(final boolean val)
	{
		_buffer.put((byte) (val ? 1 : 0));
	}
	
	protected final void writeString(final String val)
	{
		if (val != null)
			for (final char ch : val.toCharArray())
				_buffer.putChar(ch);
		_buffer.putChar('\000');
	}
}