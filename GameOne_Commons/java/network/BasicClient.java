package network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.Queue;

public abstract class BasicClient
{
	public static final int PACKET_SIZE = 32768;
	
	private final ByteBuffer _readBuffer = ByteBuffer.allocateDirect(PACKET_SIZE);
	private final ReadHandler _readHandler = new ReadHandler();
	private final WriteHandler _writeHandler = new WriteHandler();
	private final Queue<PacketWriter> _sendQueue = new LinkedList<>();
	
	private AsynchronousSocketChannel _channel;
	private boolean _pendingWrite;
	
	public final void setChannel(final AsynchronousSocketChannel channel)
	{
		_channel = channel;
		_channel.read(_readBuffer, this, _readHandler);
	}
	
	public final AsynchronousSocketChannel getChannel()
	{
		return _channel;
	}
	
	public final void readPacket()
	{
		_readBuffer.flip();
		while (_readBuffer.hasRemaining())
		{
			final int opCode = _readBuffer.getInt();
			final PacketInfo inf = PacketInfo.values()[opCode];
			final PacketReader<BasicClient> packet = inf.getReadPacket();
			
			packet.setBuffer(_readBuffer);
			packet.read();
			packet.run(this);
		}
		_readBuffer.clear();
		
		_channel.read(_readBuffer, this, _readHandler);
	}
	
	public final void sendPacket(final PacketWriter packet)
	{
		if (packet.getBuffer().limit() == PACKET_SIZE)
		{
			packet.write();
			packet.getBuffer().flip();
		}
		
		_sendQueue.add(packet);
		
		executeWriteTask();
	}
	
	public final void resetPendingWrite()
	{
		_pendingWrite = false;
	}
	
	public final synchronized void executeWriteTask()
	{
		if (!_pendingWrite && !_sendQueue.isEmpty())
		{
			_pendingWrite = true;
			
			final Queue<PacketWriter> copy = new LinkedList<>();
			int bytes = 0;
			while (!_sendQueue.isEmpty())
			{
				final PacketWriter packet = _sendQueue.poll();
				if (bytes + packet.getBuffer().limit() > PACKET_SIZE)
					break;
				
				copy.add(packet);
				bytes += packet.getBuffer().limit();
			}
			
			final ByteBuffer toSend = ByteBuffer.allocateDirect(bytes);
			for (final PacketWriter packet : copy)
				toSend.put(packet.getBuffer().duplicate());
			toSend.flip();
			
			_channel.write(toSend, this, _writeHandler);
		}
	}
	
	public abstract void onDisconnect();
}