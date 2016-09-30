package s2048;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import windows.GameSelect;
import client.Client;
import network.request.RequestUpdateGameScore;
import util.random.Rnd;

/**
 * 2048 game.
 * @author Sahar
 */
public final class S2048 extends JFrame
{
	private static final long serialVersionUID = -2474315113654316136L;
	
	private static final Logger LOGGER = Logger.getLogger(S2048.class.getName());
	private static final int SIZE = 4;
	
	private final Cell[][] _puzzle = new Cell[SIZE][SIZE];
	private int _score;
	
	private S2048()
	{
		super("GameOne Client - 2048 Puzzle");
		
		setLayout(new GridBagLayout());
		
		final GridBagConstraints gc = new GridBagConstraints();
		for (int i = 0;i < SIZE;i++)
		{
			for (int j = 0;j < SIZE;j++)
			{
				gc.gridx = i + 1;
				gc.gridy = j + 1;
				
				_puzzle[i][j] = new Cell();
				add(_puzzle[i][j], gc);
			}
		}
		
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addKeyListener(new Movement());
		
		LOGGER.info("2048Puzzle screen loaded.");
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		reset();
		Client.getInstance().setCurrentDetails(GameSelect.getInstance(), null, true);
	}
	
	public void reset()
	{
		Client.getInstance().sendPacket(new RequestUpdateGameScore(has2048Block(), _score));
		
		for (int i = 0;i < SIZE;i++)
			for (int j = 0;j < SIZE;j++)
				_puzzle[i][j].reset();
		
		_score = 0;
	}
	
	public void start()
	{
		takeRandomBlock();
		takeRandomBlock();
		
		setVisible(true);
	}
	
	private boolean has2048Block()
	{
		for (int i = 0;i < SIZE;i++)
			for (int j = 0;j < SIZE;j++)
				if (_puzzle[i][j].get() == 2048)
					return true;
		
		return false;
	}
	
	private void takeRandomBlock()
	{
		boolean hasEmptySpot = false;
		for (int i = 0;i < _puzzle.length && !hasEmptySpot;i++)
			for (int j = 0;j < _puzzle.length && !hasEmptySpot;j++)
				if (!_puzzle[i][j].taken())
					hasEmptySpot = true;
		if (!hasEmptySpot)
		{
			JOptionPane.showMessageDialog(null, "Game Over!", "Noob", JOptionPane.INFORMATION_MESSAGE);
			dispose();
			return;
		}
		
		int rndI;
		int rndJ;
		do
		{
			rndI = Rnd.get(SIZE);
			rndJ = Rnd.get(SIZE);
		} while (_puzzle[rndI][rndJ].taken());
		_puzzle[rndI][rndJ].set();
	}
	
	private void move(char dir)
	{
		switch (dir)
		{
			case 'w':
				for (int i = 0;i < _puzzle.length;i++)
				{
					for (int j = 0;j < _puzzle.length;j++)
					{
						if (j == 0)
							continue;
						if (_puzzle[i][j].get() == 0)
							continue;
						
						int y = j - 1;
						while (y >= 0 && _puzzle[i][y].get() == 0)
						{
							_puzzle[i][y].set(_puzzle[i][j], false);
							_puzzle[i][j].reset();
							j = y;
							y--;
						}
						if (j == 0)
							continue;
						
						if (_puzzle[i][j].get() == _puzzle[i][j - 1].get())
						{
							_score += _puzzle[i][j - 1].set(_puzzle[i][j], true);
							_puzzle[i][j].reset();
						}
					}
				}
				break;
			case 'a':
				for (int i = 0;i < _puzzle.length;i++)
				{
					for (int j = 0;j < _puzzle.length;j++)
					{
						if (i == 0)
							continue;
						if (_puzzle[i][j].get() == 0)
							continue;
						
						int x = i - 1;
						while (x >= 0 && _puzzle[x][j].get() == 0)
						{
							_puzzle[x][j].set(_puzzle[i][j], false);
							_puzzle[i][j].reset();
							i = x;
							x--;
						}
						if (i == 0)
							continue;
						
						if (_puzzle[i][j].get() == _puzzle[i - 1][j].get())
						{
							_score += _puzzle[i - 1][j].set(_puzzle[i][j], true);
							_puzzle[i][j].reset();
						}
					}
				}
				break;
			case 'd':
				for (int i = 3;i >= 0;i--)
				{
					for (int j = 3;j >= 0;j--)
					{
						if (i == 3)
							continue;
						if (_puzzle[i][j].get() == 0)
							continue;
						
						int x = i + 1;
						while (x < 4 && _puzzle[x][j].get() == 0)
						{
							_puzzle[x][j].set(_puzzle[i][j], false);
							_puzzle[i][j].reset();
							i = x;
							x++;
						}
						if (i == 3)
							continue;
						
						if (_puzzle[i][j].get() == _puzzle[i + 1][j].get())
						{
							_score += _puzzle[i + 1][j].set(_puzzle[i][j], true);
							_puzzle[i][j].reset();
						}
					}
				}
				break;
			case 's':
				for (int i = 3;i >= 0;i--)
				{
					for (int j = 3;j >= 0;j--)
					{
						if (j == 3)
							continue;
						if (_puzzle[i][j].get() == 0)
							continue;
						
						int y = j + 1;
						while (y < 4 && _puzzle[i][y].get() == 0)
						{
							_puzzle[i][y].set(_puzzle[i][j], false);
							_puzzle[i][j].reset();
							j = y;
							y++;
						}
						if (j == 3)
							continue;
						
						if (_puzzle[i][j].get() == _puzzle[i][j + 1].get())
						{
							_score += _puzzle[i][j + 1].set(_puzzle[i][j], true);
							_puzzle[i][j].reset();
						}
					}
				}
				break;
		}
		
		takeRandomBlock();
	}
	
	private class Movement extends KeyAdapter
	{
		@Override
		public void keyPressed(final KeyEvent e)
		{
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					move('w');
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					move('a');
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					move('d');
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					move('s');
					break;
			}
		}
	}
	
	public static S2048 getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final S2048 INSTANCE = new S2048();
	}
}