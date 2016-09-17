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

import util.threadpool.ThreadPool;

/**
 * BasicClient should be extended by both client/server.
 * @author Sahar
 */
public abstract class BasicClient
{
	private static final Logger LOGGER = Logger.getLogger(BasicClient.class.getName());
	
	private final ReadHandler<BasicClient> _readHandler;
	private final WriteHandler<BasicClient> _writeHandler;
	private final Queue<PacketWriter> _sendQueue;
	private final ReentrantLock _writeLock;
	private final ByteBuffer _readBuffer;
	
	private AsynchronousSocketChannel _channel;
	private boolean _pendingWrite;
	
	protected BasicClient()
	{
		_readBuffer = ByteBuffer.allocateDirect(1024);
		_readHandler = new ReadHandler<>();
		_writeHandler = new WriteHandler<>();
		_sendQueue = new ConcurrentLinkedQueue<>();
		_writeLock = new ReentrantLock();
	}
	
	public ByteBuffer getReadBuffer()
	{
		return _readBuffer;
	}
	
	public final ReadHandler<BasicClient> getReadHandler()
	{
		return _readHandler;
	}
	
	public final WriteHandler<BasicClient> getWriteHandler()
	{
		return _writeHandler;
	}
	
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
		packet.write();
		packet.pack();
		
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
					
					ThreadPool.execute(() ->
					{
						final Queue<PacketWriter> copy = new ConcurrentLinkedQueue<>();
						while (!_sendQueue.isEmpty())
							copy.add(_sendQueue.poll());
						
						int bytes = 0;
						for (final PacketWriter packet : copy)
							bytes += packet.getBuffer().limit();
						final ByteBuffer toSend = ByteBuffer.allocateDirect(bytes);
						for (final PacketWriter packet : copy)
							toSend.put(packet.getBuffer());
						toSend.flip();
						
						_channel.write(toSend, this, _writeHandler);
					});
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