package network;

import java.nio.ByteBuffer;

/**
 * Outgoing packet implementation.
 * @author Sahar
 */
public abstract class PacketWriter
{
	private final ByteBuffer _buf = ByteBuffer.allocateDirect(BasicClient.PACKET_SIZE);
	
	public abstract void write();
	
	public final ByteBuffer getBuffer()
	{
		return _buf;
	}
	
	protected final void writeByte(final byte val)
	{
		_buf.put(val);
	}
	
	protected final void writeInt(final int val)
	{
		_buf.putInt(val);
	}
	
	protected final void writeLong(final long val)
	{
		_buf.putLong(val);
	}
	
	protected final void writeDouble(final double val)
	{
		_buf.putDouble(val);
	}
	
	protected final void writeBoolean(final boolean val)
	{
		_buf.put((byte) (val ? 1 : 0));
	}
	
	protected final void writeString(final String val)
	{
		if (val != null)
			for (int i = 0;i < val.length();i++)
				_buf.putChar(val.charAt(i));
		_buf.putChar('\000');
	}
}