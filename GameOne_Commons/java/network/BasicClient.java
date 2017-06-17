package network;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BasicClient
{
	public static final int PACKET_SIZE = 32768;
	
	protected static final Logger LOGGER = Logger.getLogger(BasicClient.class.getName());
	
	protected final ByteBuffer _readBuffer = ByteBuffer.allocateDirect(PACKET_SIZE);
	protected final ReadHandler _readHandler = new ReadHandler();
	
	private final WriteHandler _writeHandler = new WriteHandler();
	private final Queue<PacketWriter> _sendQueue = new ConcurrentLinkedQueue<>();
	private final ReentrantLock _writeLock = new ReentrantLock();
	
	private AsynchronousSocketChannel _channel;
	private boolean _pendingWrite;
	
	public final void setChannel(final AsynchronousSocketChannel channel)
	{
		_channel = channel;
		try
		{
			_channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			_channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		}
		catch (final IOException e)
		{
			LOGGER.log(Level.WARNING, "Failed setting socket options: ", e);
		}
		
		_channel.read(_readBuffer, this, _readHandler);
	}
	
	public final AsynchronousSocketChannel getChannel()
	{
		return _channel;
	}
	
	public final void sendPacket(final PacketWriter packet)
	{
		if (!packet.packed())
		{
			packet.write();
			packet.pack();
		}
		
		_sendQueue.add(packet);
		
		executeWriteTask();
	}
	
	public final void setPendingWrite(final boolean val)
	{
		_writeLock.lock();
		
		try
		{
			_pendingWrite = val;
		}
		finally
		{
			_writeLock.unlock();
		}
	}
	
	public final void executeWriteTask()
	{
		if (!_sendQueue.isEmpty())
		{
			_writeLock.lock();
			
			try
			{
				if (!_pendingWrite)
				{
					_pendingWrite = true;
					
					final Queue<PacketWriter> copy = new ConcurrentLinkedQueue<>();
					int bytes = 0;
					while (!_sendQueue.isEmpty())
					{
						final PacketWriter packet = _sendQueue.peek();
						if (bytes + packet.getBuffer().limit() > PACKET_SIZE)
							break;
						
						copy.add(packet);
						_sendQueue.remove(packet);
						
						bytes += packet.getBuffer().limit();
					}
					
					final ByteBuffer toSend = ByteBuffer.allocateDirect(bytes);
					for (final PacketWriter packet : copy)
						toSend.put(packet.getBuffer().duplicate());
					toSend.flip();
					
					_channel.write(toSend, this, _writeHandler);
				}
			}
			finally
			{
				_writeLock.unlock();
			}
		}
	}
	
	public abstract void readPacket();
	
	public abstract void onDisconnect();
}