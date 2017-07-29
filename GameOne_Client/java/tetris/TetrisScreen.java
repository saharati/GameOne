package tetris;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import client.Client;
import network.request.RequestUpdateGameScore;
import objects.GameResult;
import util.random.Rnd;
import util.threadpool.ThreadPool;
import windows.GameSelect;

/**
 * Tetris game.
 * @author Sahar
 */
public final class TetrisScreen extends JFrame implements Runnable
{
	private static final long serialVersionUID = 2788905536064599286L;
	
	private static final Logger LOGGER = Logger.getLogger(TetrisScreen.class.getName());
	private static final char[] SHAPES = {'I', 'J', 'L', 'O', 'S', 'T', 'Z'};
	private static final Color[] COLORS = {Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED};
	private static final int ROWS = 20;
	private static final int COLS = 10;
	private static final int SHAPE_SIZE = 4;
	private static final int SLOW_SPEED_MILLIS = 250;
	private static final int FAST_SPEED_MILLIS = 50;
	
	protected ScheduledFuture<?> _moveTask;
	protected boolean _keyDownPressed;
	
	private final TetrisPanel[][] _board = new TetrisPanel[ROWS][COLS];
	private char _nextShape = SHAPES[Rnd.get(SHAPES.length)];
	private boolean _isWin;
	private int[][] _currentShape;
	private char _curShape;
	private int _score;
	private int _align;
	
	protected TetrisScreen()
	{
		super("GameOne Client - Tetris");
		
		setLayout(new GridLayout(ROWS, COLS));
		for (int i = 0;i < ROWS;i++)
		{
			for (int j = 0;j < COLS;j++)
			{
				_board[i][j] = new TetrisPanel();
				add(_board[i][j]);
			}
		}
		
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addKeyListener(new Movement());
		
		LOGGER.info("Tetris screen loaded.");
	}
	
	public void start()
	{
		setVisible(true);
		
		_moveTask = ThreadPool.schedule(this, SLOW_SPEED_MILLIS, TimeUnit.MILLISECONDS);
	}
	
	public void reset()
	{
		if (_moveTask != null)
		{
			if (!_moveTask.isCancelled())
				_moveTask.cancel(true);
			
			_moveTask = null;
		}
		
		Client.getInstance().sendPacket(new RequestUpdateGameScore(_isWin ? GameResult.WIN : GameResult.LOSE, _score));
		
		for (int i = 0;i < ROWS;i++)
			for (int j = 0;j < COLS;j++)
				_board[i][j].setTakenBy(Color.LIGHT_GRAY, true);
		_isWin = false;
		_keyDownPressed = false;
		_currentShape = null;
		_curShape = 0;
		_align = 0;
		_score = 0;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		reset();
		Client.getInstance().setCurrentDetails(GameSelect.getInstance(), null, true);
	}
	
	@Override
	public void run()
	{
		if (_currentShape == null)
		{
			if (!tryPositionNextShape())
			{
				JOptionPane.showMessageDialog(null, "Shape blocked.", "You lose!", JOptionPane.INFORMATION_MESSAGE);
				
				dispose();
				return;
			}
		}
		else if (dropShape())
		{
			checkLineBreak();
			
			_currentShape = null;
			_curShape = 0;
			_align = 0;
		}
		
		_moveTask = ThreadPool.schedule(this, _keyDownPressed ? FAST_SPEED_MILLIS : SLOW_SPEED_MILLIS, TimeUnit.MILLISECONDS);
	}
	
	private boolean tryPositionNextShape()
	{
		final int index = Rnd.get(COLS - SHAPE_SIZE);
		
		_currentShape = new int[SHAPE_SIZE][2];
		if (_nextShape == 'I')
		{
			for (int i = 0;i < 4;i++)
			{
				_currentShape[i][0] = 0;
				_currentShape[i][1] = index + i;
			}
		}
		else if (_nextShape == 'J')
		{
			for (int i = 0;i < 3;i++)
			{
				_currentShape[i][0] = 0;
				_currentShape[i][1] = index + i;
			}
			_currentShape[3][0] = 1;
			_currentShape[3][1] = index + 2;
		}
		else if (_nextShape == 'L')
		{
			for (int i = 0;i < 3;i++)
			{
				_currentShape[i][0] = 0;
				_currentShape[i][1] = index + i;
			}
			_currentShape[3][0] = 1;
			_currentShape[3][1] = index;
		}
		else if (_nextShape == 'O')
		{
			for (int i = 0;i < 2;i++)
			{
				_currentShape[i][0] = 0;
				_currentShape[i][1] = index + i;
			}
			for (int i = 2;i < 4;i++)
			{
				_currentShape[i][0] = 1;
				_currentShape[i][1] = index + i - 2;
			}
		}
		else if (_nextShape == 'S')
		{
			for (int i = 0;i < 2;i++)
			{
				_currentShape[i][0] = 1;
				_currentShape[i][1] = index + i;
			}
			for (int i = 2;i < 4;i++)
			{
				_currentShape[i][0] = 0;
				_currentShape[i][1] = index + i - 1;
			}
		}
		else if (_nextShape == 'T')
		{
			for (int i = 0;i < 3;i++)
			{
				_currentShape[i][0] = 0;
				_currentShape[i][1] = index + i;
			}
			_currentShape[3][0] = 1;
			_currentShape[3][1] = index + 1;
		}
		else if (_nextShape == 'Z')
		{
			for (int i = 2;i < 4;i++)
			{
				_currentShape[i][0] = 1;
				_currentShape[i][1] = index + i - 1;
			}
			for (int i = 0;i < 2;i++)
			{
				_currentShape[i][0] = 0;
				_currentShape[i][1] = index + i;
			}
		}
		
		boolean ret = true;
		for (int i = 0;i < SHAPE_SIZE;i++)
		{
			if (_board[_currentShape[i][0]][_currentShape[i][1]].isTaken())
				ret = false;
			
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(getCurrentColor(), true);
		}
		
		_curShape = _nextShape;
		_nextShape = SHAPES[Rnd.get(SHAPES.length)];
		
		return ret;
	}

	private boolean dropShape()
	{
		boolean ret = false;
		for (int i = 0;i < SHAPE_SIZE;i++)
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(Color.LIGHT_GRAY, false);
		for (int i = 0;i < SHAPE_SIZE;i++)
			if (_currentShape[i][0] + 1 == ROWS || _board[_currentShape[i][0] + 1][_currentShape[i][1]].isTaken())
				ret = true;
		if (ret)
		{
			for (int i = 0;i < SHAPE_SIZE;i++)
				_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(getCurrentColor(), false);
			
			return true;
		}
		
		for (int i = 0;i < SHAPE_SIZE;i++)
		{
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(Color.LIGHT_GRAY, true);
			_currentShape[i][0]++;
		}
		for (int i = 0;i < SHAPE_SIZE;i++)
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(getCurrentColor(), true);
		
		return false;
	}
	
	private void checkLineBreak()
	{
		for (int i = 0;i < ROWS;i++)
			checkLine(_board[i], i);
	}
	
	private void checkLine(final TetrisPanel[] line, final int i)
	{
		boolean full = true;
		for (final TetrisPanel l : line)
		{
			if (!l.isTaken())
			{
				full = false;
				break;
			}
		}
		if (full)
		{
			for (final TetrisPanel l : line)
				l.setTakenBy(Color.LIGHT_GRAY, true);
			
			lowerAboveLines(i - 1);
			
			_score++;
			if (isBoardEmpty())
				_isWin = true;
		}
	}
	
	private void lowerAboveLines(int line)
	{
		for (;line > -1;line--)
		{
			for (int i = 0;i < COLS;i++)
			{
				if (_board[line][i].isTaken())
				{
					final Color toPut = _board[line][i].getBackground();
					
					_board[line][i].setTakenBy(Color.LIGHT_GRAY, true);
					_board[line + 1][i].setTakenBy(toPut, true);
				}
			}
		}
	}
	
	private boolean isBoardEmpty()
	{
		for (final TetrisPanel[] line : _board)
			for (final TetrisPanel l : line)
				if (l.isTaken())
					return false;
		
		return true;
	}
	
	private Color getCurrentColor()
	{
		for (int i = 0;i < SHAPES.length;i++)
			if (SHAPES[i] == _curShape)
				return COLORS[i];
		
		return Color.LIGHT_GRAY;
	}
	
	protected void tryMoveToSide(int movement)
	{
		if (_currentShape == null)
			return;
		
		boolean ret = false;
		for (int i = 0;i < SHAPE_SIZE;i++)
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(Color.LIGHT_GRAY, false);
		for (int i = 0;i < SHAPE_SIZE;i++)
			if (_currentShape[i][1] + movement == COLS || _currentShape[i][1] + movement == -1 || _board[_currentShape[i][0]][_currentShape[i][1] + movement].isTaken())
				ret = true;
		if (ret)
		{
			for (int i = 0;i < SHAPE_SIZE;i++)
				_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(getCurrentColor(), false);
			
			return;
		}
		
		for (int i = 0;i < SHAPE_SIZE;i++)
		{
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(Color.LIGHT_GRAY, true);
			_currentShape[i][1] = _currentShape[i][1] + movement;
		}
		for (int i = 0;i < SHAPE_SIZE;i++)
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(getCurrentColor(), true);
	}
	
	protected void trySwitchingAlignment()
	{
		if (_currentShape == null || _curShape == 'O')
			return;
		
		int align = _align;
		int[][] temp = new int[SHAPE_SIZE][2];
		int[] firstIndex = new int[2];
		for (int i = 0;i < SHAPE_SIZE;i++)
		{
			System.arraycopy(_currentShape[i], 0, temp[i], 0, _currentShape[i].length);
			if (i == 0)
				System.arraycopy(temp[0], 0, firstIndex, 0, temp[0].length);
			
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(Color.LIGHT_GRAY, false);
		}
		
		if (_curShape == 'I')
		{
			if (align == 0)
			{
				for (int i = 1;i < 4;i++)
				{
					temp[i][0] = ++firstIndex[0];
					temp[i][1] = firstIndex[1];
				}
				align++;
			}
			else
			{
				for (int i = 1;i < 4;i++)
				{
					temp[i][0] = firstIndex[0];
					temp[i][1] = ++firstIndex[1];
				}
				align--;
			}
		}
		else if (_curShape == 'J')
		{
			if (align == 0)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = ++firstIndex[0];
					temp[i][1] = firstIndex[1];
				}
				temp[3][0] = firstIndex[0];
				temp[3][1] = --firstIndex[1];
				align++;
			}
			else if (align == 1)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = firstIndex[0];
					temp[i][1] = --firstIndex[1];
				}
				temp[3][0] -= 3;
				temp[3][1]--;
				align++;
			}
			else if (align == 2)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = --firstIndex[0];
					temp[i][1] = firstIndex[1];
				}
				temp[3][0] = firstIndex[0];
				temp[3][1] = ++firstIndex[1];
				align++;
			}
			else
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = firstIndex[0];
					temp[i][1] = ++firstIndex[1];
				}
				temp[3][0] += 3;
				temp[3][1]++;
				align = 0;
			}
		}
		else if (_curShape == 'L')
		{
			if (align == 0)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = ++firstIndex[0];
					temp[i][1] = firstIndex[1];
				}
				temp[3][0] = firstIndex[0];
				temp[3][1] = ++firstIndex[1];
				align++;
			}
			else if (align == 1)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = firstIndex[0];
					temp[i][1] = ++firstIndex[1];
				}
				temp[3][0] -= 3;
				temp[3][1]++;
				align++;
			}
			else if (align == 2)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = --firstIndex[0];
					temp[i][1] = firstIndex[1];
				}
				temp[3][0] = firstIndex[0];
				temp[3][1] = --firstIndex[1];
				align++;
			}
			else
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = firstIndex[0];
					temp[i][1] = --firstIndex[1];
				}
				temp[3][0] += 3;
				temp[3][1]--;
				align = 0;
			}
		}
		else if (_curShape == 'S')
		{
			if (align == 0)
			{
				temp[1][0] = firstIndex[0] - 1;
				temp[1][1] = firstIndex[1];
				for (int i = 2;i < 4;i++)
				{
					temp[i][0] = firstIndex[0] + i - 2;
					temp[i][1] = firstIndex[1] + 1;
				}
				align++;
			}
			else
			{
				temp[1][0] = firstIndex[0];
				temp[1][1] = firstIndex[1] + 1;
				for (int i = 2;i < 4;i++)
				{
					temp[i][0] = firstIndex[0] + 1;
					temp[i][1] = firstIndex[1] - i + 2;
				}
				align--;
			}
		}
		else if (_curShape == 'T')
		{
			if (align == 0)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = ++firstIndex[0];
					temp[i][1] = firstIndex[1];
				}
				temp[3][0] = --firstIndex[0];
				temp[3][1] = --firstIndex[1];
				align++;
			}
			else if (align == 1)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = firstIndex[0];
					temp[i][1] = ++firstIndex[1];
				}
				temp[3][0] -= 2;
				temp[3][1] += 2;
				align++;
			}
			else if (align == 2)
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = --firstIndex[0];
					temp[i][1] = firstIndex[1];
				}
				temp[3][0] = ++firstIndex[0];
				temp[3][1] = ++firstIndex[1];
				align++;
			}
			else
			{
				for (int i = 1;i < 3;i++)
				{
					temp[i][0] = firstIndex[0];
					temp[i][1] = --firstIndex[1];
				}
				temp[3][0] += 2;
				temp[3][1] -= 2;
				align = 0;
			}
		}
		else if (_curShape == 'Z')
		{
			if (align == 0)
			{
				temp[1][0] = firstIndex[0] - 1;
				temp[1][1] = firstIndex[1];
				for (int i = 2;i < 4;i++)
				{
					temp[i][0] = firstIndex[0] + i - 2;
					temp[i][1] = firstIndex[1] - 1;
				}
				align++;
			}
			else
			{
				temp[1][0] = firstIndex[0];
				temp[1][1] = firstIndex[1] + 1;
				for (int i = 2;i < 4;i++)
				{
					temp[i][0] = firstIndex[0] - 1;
					temp[i][1] = firstIndex[1] - i + 2;
				}
				align--;
			}
		}
		
		for (int i = 0;i < SHAPE_SIZE;i++)
		{
			if (temp[i][0] > ROWS - 1 || temp[i][0] < 0 || temp[i][1] < 0 || temp[i][1] > COLS - 1 || _board[temp[i][0]][temp[i][1]].isTaken())
			{
				for (int j = 0;j < SHAPE_SIZE;j++)
					_board[_currentShape[j][0]][_currentShape[j][1]].setTakenBy(getCurrentColor(), false);
				
				return;
			}
		}
		
		for (int i = 0;i < SHAPE_SIZE;i++)
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(Color.LIGHT_GRAY, true);
		_currentShape = temp;
		_align = align;
		for (int i = 0;i < SHAPE_SIZE;i++)
			_board[_currentShape[i][0]][_currentShape[i][1]].setTakenBy(getCurrentColor(), true);
	}
	
	protected void pause()
	{
		if (_moveTask.isCancelled())
			_moveTask = ThreadPool.schedule(this, _keyDownPressed ? FAST_SPEED_MILLIS : SLOW_SPEED_MILLIS, TimeUnit.MILLISECONDS);
		else
			_moveTask.cancel(false);
	}
	
	protected class Movement extends KeyAdapter
	{
		@Override
		public void keyReleased(final KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				_keyDownPressed = false;
		}
		
		@Override
		public void keyPressed(final KeyEvent e)
		{
			if (_moveTask.isCancelled() && e.getKeyCode() != KeyEvent.VK_P)
				return;
			
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_DOWN:
					_keyDownPressed = true;
					break;
				case KeyEvent.VK_SPACE:
					trySwitchingAlignment();
					break;
				case KeyEvent.VK_LEFT:
					tryMoveToSide(-1);
					break;
				case KeyEvent.VK_RIGHT:
					tryMoveToSide(1);
					break;
				case KeyEvent.VK_P:
					pause();
					break;
			}
		}
	}
	
	public static TetrisScreen getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TetrisScreen INSTANCE = new TetrisScreen();
	}
}