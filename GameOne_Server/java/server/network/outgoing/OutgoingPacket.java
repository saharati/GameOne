package server.network.outgoing;

import java.nio.ByteBuffer;

import server.network.GameClient;

/**
 * Outgoing packet implementation.
 * @author Sahar
 */
public abstract class OutgoingPacket
{
	private final ByteBuffer _buf = ByteBuffer.allocateDirect(GameClient.BUFFER_SIZE);
	
	public abstract void write();
	
	public final ByteBuffer getBuffer()
	{
		return _buf;
	}
	
	public final int getRemainingBytes()
	{
		return _buf.remaining();
	}
	
	protected final void writeByte(final byte val)
	{
		_buf.put(val);
	}
	
	protected final void writeShort(final short val)
	{
		_buf.putShort(val);
	}
	
	protected final void writeInt(final int val)
	{
		_buf.putInt(val);
	}
	
	protected final void writeLong(final long val)
	{
		_buf.putLong(val);
	}
	
	protected final void writeFloat(final float val)
	{
		_buf.putFloat(val);
	}
	
	protected final void writeDouble(final double val)
	{
		_buf.putDouble(val);
	}
	
	protected final void writeChar(final char val)
	{
		_buf.putChar(val);
	}
	
	protected final void writeString(final String val)
	{
		if (val != null)
		{
			final int len = val.length();
			for (int i = 0;i < len;i++)
				_buf.putChar(val.charAt(i));
		}
		
		_buf.putChar('\000');
	}
	
	protected final void writeBytes(final byte[] val)
	{
		_buf.put(val);
	}
}