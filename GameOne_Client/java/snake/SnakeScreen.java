package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import client.Client;
import network.request.RequestUpdateGameScore;
import objects.GameResult;
import util.random.Rnd;
import util.threadpool.ThreadPool;
import windows.GameSelect;

/**
 * Snake game.
 * @author Sahar
 */
public final class SnakeScreen extends JFrame implements Runnable
{
	private static final long serialVersionUID = -7154700434188653121L;
	
	private static final Logger LOGGER = Logger.getLogger(SnakeScreen.class.getName());
	private static final int SCREEN_SIZE = 25;
	private static final int SPEED_IN_MILLISECONDS = 75;
	private static final Dimension BLOCK_SIZE = new Dimension(20, 20);
	
	protected final LinkedList<int[]> _snake = new LinkedList<>();
	protected ScheduledFuture<?> _moveTask;
	protected int _lastAlign;
	protected int _currentAlign;
	
	private final JPanel[][] _board = new JPanel[SCREEN_SIZE][SCREEN_SIZE];
	private final int[] _food = new int[2];
	private int _score;
	
	protected SnakeScreen()
	{
		super("GameOne Client - Snake");
		
		for (int i = 0;i < SCREEN_SIZE;i++)
		{
			for (int j = 0;j < SCREEN_SIZE;j++)
			{
				_board[i][j] = new JPanel();
				_board[i][j].setPreferredSize(BLOCK_SIZE);
				 add(_board[i][j]);
			}
		}
		
		setLayout(new GridLayout(SCREEN_SIZE, SCREEN_SIZE));
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addKeyListener(new Movement());
		
		LOGGER.info("Snake screen loaded.");
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		reset(false);
		Client.getInstance().setCurrentDetails(GameSelect.getInstance(), null, true);
	}
	
	public void reset(final boolean logout)
	{
		if (!logout)
			Client.getInstance().sendPacket(new RequestUpdateGameScore(isFull() ? GameResult.WIN : GameResult.LOSE, _score));
		
		_snake.clear();
		if (_moveTask != null)
		{
			if (!_moveTask.isCancelled())
				_moveTask.cancel(false);
			
			_moveTask = null;
		}
		_lastAlign = 0;
		_currentAlign = 0;
		_score = 0;
	}
	
	public void start()
	{
		for (int i = 0;i < SCREEN_SIZE;i++)
			for (int j = 0;j < SCREEN_SIZE;j++)
				_board[i][j].setBackground(Color.LIGHT_GRAY);
		
		final int startX = Rnd.get(SCREEN_SIZE - 6) + 4;
		final int startY = Rnd.get(SCREEN_SIZE - 6) + 4;
		final boolean horizontal = Rnd.nextBoolean();
		int direction = Rnd.nextBoolean() ? 1 : -1;
		
		_snake.add(new int[] {startX, startY});
		for (int i = 0;i < 2;i++)
		{
			if (horizontal)
				_snake.add(new int[] {startX + direction, startY});
			else
				_snake.add(new int[] {startX, startY + direction});
			
			if (direction < 0)
				direction--;
			else
				direction++;
		}
		for (final int[] part : _snake)
			_board[part[0]][part[1]].setBackground(Color.BLACK);
		_board[_snake.getFirst()[0]][_snake.getFirst()[1]].setBackground(Color.GREEN);
		
		makeFood();
		
		setVisible(true);
	}
	
	private boolean isFull()
	{
		for (int i = 0;i < SCREEN_SIZE;i++)
			for (int j = 0;j < SCREEN_SIZE;j++)
				if (_board[i][j].getBackground() == Color.LIGHT_GRAY)
					return false;
		
		return true;
	}
	
	private void makeFood()
	{
		do
		{
			_food[0] = Rnd.get(SCREEN_SIZE);
			_food[1] = Rnd.get(SCREEN_SIZE);
		} while (_board[_food[0]][_food[1]].getBackground() != Color.LIGHT_GRAY);
		_board[_food[0]][_food[1]].setBackground(Color.RED);
	}
	
	protected boolean moveSnake()
	{
		final int[] tail = _snake.removeLast();
		_board[tail[0]][tail[1]].setBackground(Color.LIGHT_GRAY);
		
		int[] head = _snake.getFirst();
		
		_currentAlign = _lastAlign;
		switch (_currentAlign)
		{
			case KeyEvent.VK_DOWN:
				_snake.addFirst(new int[] {head[0] + 1, head[1]});
				break;
			case KeyEvent.VK_UP:
				_snake.addFirst(new int[] {head[0] - 1, head[1]});
				break;
			case KeyEvent.VK_LEFT:
				_snake.addFirst(new int[] {head[0], head[1] - 1});
				break;
			case KeyEvent.VK_RIGHT:
				_snake.addFirst(new int[] {head[0], head[1] + 1});
				break;
		}
		head = _snake.getFirst();
		if (head[0] < 0 || head[1] < 0 || head[0] >= SCREEN_SIZE || head[1] >= SCREEN_SIZE)
			return false;
		
		final Color col = _board[head[0]][head[1]].getBackground();
		if (col == Color.BLACK)
			return false;
		
		_board[_snake.get(1)[0]][_snake.get(1)[1]].setBackground(Color.BLACK);
		_board[head[0]][head[1]].setBackground(Color.GREEN);
		
		if (col == Color.RED)
		{
			_score++;
			
			_snake.add(tail);
			_board[tail[0]][tail[1]].setBackground(Color.BLACK);
			
			makeFood();
		}
		
		return true;
	}
	
	@Override
	public void run()
	{
		if (!moveSnake())
		{
			_moveTask.cancel(false);
			
			JOptionPane.showMessageDialog(null, "You gone out of boundaries or you ate yourself.", "You lose!", JOptionPane.INFORMATION_MESSAGE);
			
			dispose();
		}
	}
	
	protected class Movement extends KeyAdapter
	{
		@Override
		public void keyPressed(final KeyEvent e)
		{
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_RIGHT:
					if (e.getKeyCode() == KeyEvent.VK_DOWN && _currentAlign == KeyEvent.VK_UP)
						return;
					if (e.getKeyCode() == KeyEvent.VK_LEFT && _currentAlign == KeyEvent.VK_RIGHT)
						return;
					if (e.getKeyCode() == KeyEvent.VK_RIGHT && _currentAlign == KeyEvent.VK_LEFT)
						return;
					if (e.getKeyCode() == KeyEvent.VK_UP && _currentAlign == KeyEvent.VK_DOWN)
						return;
					
					_lastAlign = e.getKeyCode();
					if (_moveTask == null)
					{
						if (!moveSnake())
							Collections.reverse(_snake);
						
						_moveTask = ThreadPool.scheduleAtFixedRate(SnakeScreen.this, SPEED_IN_MILLISECONDS, SPEED_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
					}
					break;
				case KeyEvent.VK_P:
					if (_moveTask == null)
						return;
					
					if (_moveTask.isCancelled())
						_moveTask = ThreadPool.scheduleAtFixedRate(SnakeScreen.this, SPEED_IN_MILLISECONDS, SPEED_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
					else
						_moveTask.cancel(false);
					break;
			}
		}
	}
	
	public static SnakeScreen getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SnakeScreen INSTANCE = new SnakeScreen();
	}
}