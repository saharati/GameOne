package checkers;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import client.Client;
import network.request.RequestTurnChange;
import network.request.RequestUpdateGameScore;
import network.request.RequestWaitingRoom;
import objects.GameId;
import objects.GameResult;
import windows.WaitingRoom;

/**
 * Checkers main frame.
 * @author Sahar
 */
public final class CheckersScreen extends JFrame
{
	private static final long serialVersionUID = -4162982244689099156L;
	private static final Logger LOGGER = Logger.getLogger(CheckersScreen.class.getName());
	private static final String IMAGE_PATH = "./images/checkers/";
	private static final int BOARD_SIZE = 8;
	
	public static final Map<String, Image> IMAGES = new HashMap<>();
	static
	{
		for (final File file : new File(IMAGE_PATH).listFiles())
			IMAGES.put(file.getName().substring(0, file.getName().lastIndexOf('.')), new ImageIcon(file.getAbsolutePath()).getImage());
	}
	
	protected final CheckersButton[][] _buttons = new CheckersButton[BOARD_SIZE][BOARD_SIZE];
	protected Map<int[], int[]> _possibleEats = new HashMap<>();
	protected List<int[]> _possibleRoute;
	protected String _myColor;
	protected boolean _myTurn;
	protected String _selectedSoldierName;
	protected int[] _selectedSoldierPosition;
	
	protected CheckersScreen()
	{
		super("GameOne Client - Checkers");
		
		setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		LOGGER.info("Checkers screen loaded.");
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LEAVE, calcScore()));
		Client.getInstance().setCurrentDetails(WaitingRoom.getInstance(), GameId.CHECKERS, false);
	}
	
	public void start(final String myColor)
	{
		getContentPane().removeAll();
		
		_myColor = myColor;
		_myTurn = myColor.equals("white");
		
		boolean turn = false;
		
		if (_myTurn)
		{
			for (int i = 0;i < BOARD_SIZE;i++)
			{
				for (int j = 0;j < BOARD_SIZE;j++)
				{
					// White blocks.
					if (turn)
						_buttons[i][j] = new CheckersButton(null, turn);
					// Empty blocks.
					else if (i == 3 || i == 4)
					{
						_buttons[i][j] = new CheckersButton(null, turn);
						_buttons[i][j].addMouseListener(new CellClick(i, j));
					}
					// Blocks with players.
					else
					{
						if (i < 3)
							_buttons[i][j] = new CheckersButton("soldier-black", turn);
						else
							_buttons[i][j] = new CheckersButton("soldier-white", turn);
						
						_buttons[i][j].addMouseListener(new CellClick(i, j));
					}
					
					add(_buttons[i][j]);
					
					turn = !turn;
				}
				
				turn = !turn;
			}
		}
		else
		{
			for (int i = BOARD_SIZE - 1;i >= 0;i--)
			{
				for (int j = BOARD_SIZE - 1;j >= 0;j--)
				{
					// White blocks.
					if (turn)
						_buttons[i][j] = new CheckersButton(null, turn);
					// Empty blocks.
					else if (i == 3 || i == 4)
					{
						_buttons[i][j] = new CheckersButton(null, turn);
						_buttons[i][j].addMouseListener(new CellClick(i, j));
					}
					// Blocks with players.
					else
					{
						if (i < 3)
							_buttons[i][j] = new CheckersButton("soldier-black", turn);
						else
							_buttons[i][j] = new CheckersButton("soldier-white", turn);
						
						_buttons[i][j].addMouseListener(new CellClick(i, j));
					}
					
					add(_buttons[i][j]);
					
					turn = !turn;
				}
				
				turn = !turn;
			}
		}
		
		revalidate();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void showResult(final GameResult result)
	{
		switch (result)
		{
			case WIN:
				JOptionPane.showMessageDialog(null, "Congratulations, you win!", "Victory", JOptionPane.INFORMATION_MESSAGE);
				break;
			case LEAVE:
				JOptionPane.showMessageDialog(null, "Your opponent has logged off, you won!", "Victory", JOptionPane.INFORMATION_MESSAGE);
				break;
			case EXIT:
				Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.EXIT, calcScore()));
				return;
		}
		
		setVisible(false);
		
		Client.getInstance().setCurrentDetails(WaitingRoom.getInstance(), GameId.CHECKERS, false);
		Client.getInstance().sendPacket(new RequestWaitingRoom(GameId.CHECKERS));
	}
	
	public void updateData(final String image, final int[][] moves)
	{
		// [0] - From.
		// [1] - To.
		// [2] - Eat, if exists.
		_buttons[moves[0][0]][moves[0][1]].setBackground(Color.PINK);
		_buttons[moves[1][0]][moves[1][1]].setBackground(Color.MAGENTA);
		_buttons[moves[0][0]][moves[0][1]].setImage(null, true);
		_buttons[moves[1][0]][moves[1][1]].setImage(image, true);
		if (moves[2][0] != -1)
		{
			_buttons[moves[2][0]][moves[2][1]].setBackground(Color.RED);
			_buttons[moves[2][0]][moves[2][1]].setImage(null, true);
		}
		
		Toolkit.getDefaultToolkit().beep();
		
		_myTurn = true;
		
		if (lost())
		{
			Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LOSE, calcScore()));
			
			JOptionPane.showMessageDialog(null, "You are out of soldiers.", "You lost!", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public int calcScore()
	{
		return Math.max(getScoreOfColor("black"), getScoreOfColor("white"));
	}
	
	protected List<int[]> movementHandler(int i, int j)
	{
		final List<int[]> path = new ArrayList<>();
		
		// If its queen...
		if (_buttons[i][j].getName().equals("queen"))
		{
			// Flag for marking that a queen cannot eat more than one soldier.
			boolean engaged = false;
			
			// Search main diagonal.
			for (int x = i + 1, y = j + 1;x < BOARD_SIZE && y < BOARD_SIZE;x++, y++)
			{
				// If spot taken by self, break.
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				// If spot taken by enemy.
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					if (engaged)
						break;
					
					// Allow to eat only if there's no other soldier behind it.
					if (x + 1 < BOARD_SIZE && y + 1 < BOARD_SIZE && !isTakenBy("black", x + 1, y + 1) && !isTakenBy("white", x + 1, y + 1))
					{
						final int[] goIndex = new int[] {x + 1, y + 1};
						final int[] eatIndex = new int[] {x, y};
						_possibleEats.put(goIndex, eatIndex);
						path.add(goIndex);
						
						x++;
						y++;
						engaged = true;
						continue;
					}
					
					// Break either way.
					break;
				}
				
				// Add current cell.
				path.add(new int[] {x, y});
			}
			engaged = false;
			for (int x = i - 1, y = j - 1;x >= 0 && y >= 0;x--, y--)
			{
				// If spot taken by self, break.
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				// If spot taken by enemy.
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					if (engaged)
						break;
					
					// Allow to eat only if there's no other soldier behind it.
					if (x - 1 >= 0 && y - 1 >= 0 && !isTakenBy("black", x - 1, y - 1) && !isTakenBy("white", x - 1, y - 1))
					{
						final int[] goIndex = new int[] {x - 1, y - 1};
						final int[] eatIndex = new int[] {x, y};
						_possibleEats.put(goIndex, eatIndex);
						path.add(goIndex);
						
						x--;
						y--;
						engaged = true;
						continue;
					}
					
					// Break either way.
					break;
				}
				
				// Add current cell.
				path.add(new int[] {x, y});
			}
			// Search sub diagonal.
			engaged = false;
			for (int x = i + 1, y = j - 1;x < BOARD_SIZE && y >= 0;x++, y--)
			{
				// If spot taken by self, break.
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				// If spot taken by enemy.
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					if (engaged)
						break;
					
					// Allow to eat only if there's no other soldier behind it.
					if (x + 1 < BOARD_SIZE && y - 1 >= 0 && !isTakenBy("black", x + 1, y - 1) && !isTakenBy("white", x + 1, y - 1))
					{
						final int[] goIndex = new int[] {x + 1, y - 1};
						final int[] eatIndex = new int[] {x, y};
						_possibleEats.put(goIndex, eatIndex);
						path.add(goIndex);
						
						x++;
						y--;
						engaged = true;
						continue;
					}
					
					// Break either way.
					break;
				}
				
				// Add current cell.
				path.add(new int[] {x, y});
			}
			engaged = false;
			for (int x = i - 1, y = j + 1;x >= 0 && y < BOARD_SIZE;x--, y++)
			{
				// If spot taken by self, break.
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				// If spot taken by enemy.
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					if (engaged)
						break;
					
					// Allow to eat only if there's no other soldier behind it.
					if (x - 1 >= 0 && y + 1 < BOARD_SIZE && !isTakenBy("black", x - 1, y + 1) && !isTakenBy("white", x - 1, y + 1))
					{
						final int[] goIndex = new int[] {x - 1, y + 1};
						final int[] eatIndex = new int[] {x, y};
						_possibleEats.put(goIndex, eatIndex);
						path.add(goIndex);
						
						x--;
						y++;
						engaged = true;
						continue;
					}
					
					// Break either way.
					break;
				}
				
				// Add current cell.
				path.add(new int[] {x, y});
			}
		}
		// Regular soldier.
		else
		{
			// If its white it can only go up.
			if (_buttons[i][j].getColor().equals("white"))
			{
				i--;
				if (i == -1)
					return path;
				
				// Check left.
				if (j - 1 >= 0)
				{
					// If taken by enemy.
					if (isTakenBy("black", i, j - 1))
					{
						// If Still not out of boundaries and not taken by anyone...
						if (i - 1 >= 0 && j - 2 >= 0 && !isTakenBy("black", i - 1, j - 2) && !isTakenBy("black", i - 1, j - 2))
						{
							// Add the ability to eat.
							final int[] goIndex = new int[] {i - 1, j - 2};
							final int[] eatIndex = new int[] {i, j - 1};
							_possibleEats.put(goIndex, eatIndex);
							path.add(goIndex);
						}
					}
					// Add path if not taken by self.
					else if (!isTakenBy("white", i, j - 1))
						path.add(new int[] {i, j - 1});
				}
				// Check right.
				if (j + 1 < BOARD_SIZE)
				{
					// If taken by enemy.
					if (isTakenBy("black", i, j + 1))
					{
						// If Still not out of boundaries and not taken by anyone...
						if (i - 1 >= 0 && j + 2 < BOARD_SIZE && !isTakenBy("black", i - 1, j + 2) && !isTakenBy("black", i - 1, j + 2))
						{
							// Add the ability to eat.
							final int[] goIndex = new int[] {i - 1, j + 2};
							final int[] eatIndex = new int[] {i, j + 1};
							_possibleEats.put(goIndex, eatIndex);
							path.add(goIndex);
						}
					}
					// Add path if not taken by self.
					else if (!isTakenBy("white", i, j + 1))
						path.add(new int[] {i, j + 1});
				}
			}
			// Its black and it can only go down.
			else
			{
				i++;
				if (i == BOARD_SIZE)
					return path;
				
				// Check left.
				if (j - 1 >= 0)
				{
					// If taken by enemy.
					if (isTakenBy("white", i, j - 1))
					{
						// If Still not out of boundaries and not taken by anyone...
						if (j - 2 >= 0 && i + 1 < BOARD_SIZE && !isTakenBy("white", i + 1, j - 2) && !isTakenBy("white", i + 1, j - 2))
						{
							// Add the ability to eat.
							final int[] goIndex = new int[] {i + 1, j - 2};
							final int[] eatIndex = new int[] {i, j - 1};
							_possibleEats.put(goIndex, eatIndex);
							path.add(goIndex);
						}
					}
					// Add path if not taken by self.
					else if (!isTakenBy("black", i, j - 1))
						path.add(new int[] {i, j - 1});
				}
				// Check right.
				if (j + 1 < BOARD_SIZE)
				{
					// If taken by enemy.
					if (isTakenBy("white", i, j + 1))
					{
						// If Still not out of boundaries and not taken by anyone...
						if (j + 2 < BOARD_SIZE && i + 1 < BOARD_SIZE && !isTakenBy("white", i + 1, j + 2) && !isTakenBy("white", i + 1, j + 2))
						{
							// Add the ability to eat.
							final int[] goIndex = new int[] {i + 1, j + 2};
							final int[] eatIndex = new int[] {i, j + 1};
							_possibleEats.put(goIndex, eatIndex);
							path.add(goIndex);
						}
					}
					// Add path if not taken by self.
					else if (!isTakenBy("black", i, j + 1))
						path.add(new int[] {i, j + 1});
				}
			}
		}
		
		return path;
	}
	
	protected boolean canMove(final int i, final int j)
	{
		for (final int[] route : _possibleRoute)
			if (route[0] == i && route[1] == j)
				return true;
		
		return false;
	}
	
	protected int[] getEatIndex(final int i, final int j)
	{
		for (final Entry<int[], int[]> eats : _possibleEats.entrySet())
			if (eats.getKey()[0] == i && eats.getKey()[1] == j)
				return eats.getValue();
		
		return null;
	}
	
	private boolean isTakenBy(final String color, final int i, final int j)
	{
		return color.equals(_buttons[i][j].getColor());
	}
	
	private boolean lost()
	{
		for (int i = 0;i < BOARD_SIZE;i++)
			for (int j = 0;j < BOARD_SIZE;j++)
				if (_myColor.equals(_buttons[i][j].getColor()))
					return false;
		
		return true;
	}
	
	private int getScoreOfColor(final String color)
	{
		int score = 0;
		for (int i = 0;i < BOARD_SIZE;i++)
			for (int j = 0;j < BOARD_SIZE;j++)
				if (color.equals(_buttons[i][j].getColor()))
					score++;
		
		return score;
	}
	
	private class CellClick extends MouseAdapter
	{
		private int _i;
		private int _j;
		
		protected CellClick(final int i, final int j)
		{
			_i = i;
			_j = j;
		}
		
		@Override
		public void mousePressed(final MouseEvent e)
		{
			if (!_myTurn)
			{
				JOptionPane.showMessageDialog(null, "This is not your turn, please wait.", "Wait", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (_selectedSoldierName == null)
			{
				if (_buttons[_i][_j].getName() == null)
				{
					JOptionPane.showMessageDialog(null, "Please select a soldier to move first.", "Select a soldier", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!_buttons[_i][_j].getColor().equals(_myColor))
				{
					JOptionPane.showMessageDialog(null, "This is not your soldier.", "Illegal action", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				_possibleRoute = movementHandler(_i, _j);
				if (_possibleRoute.size() == 0)
				{
					JOptionPane.showMessageDialog(null, "This soldier cannot move anywhere.", "Cannot move", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				for (final int[] route : _possibleRoute)
					_buttons[route[0]][route[1]].setBackground(Color.YELLOW);
				_buttons[_i][_j].setBackground(Color.ORANGE);
				
				_selectedSoldierName = _buttons[_i][_j].getFullName();
				_selectedSoldierPosition = new int[] {_i, _j};
			}
			else
			{
				if (_buttons[_i][_j].getBackground().equals(Color.ORANGE))
				{
					for (final int[] route : _possibleRoute)
						_buttons[route[0]][route[1]].setBackground(_buttons[route[0]][route[1]].getBackgroundColor());
					_selectedSoldierName = null;
					_selectedSoldierPosition = null;
					_possibleRoute.clear();
					_possibleEats.clear();
					_buttons[_i][_j].setBackground(_buttons[_i][_j].getBackgroundColor());
					return;
				}
				if (!canMove(_i, _j))
				{
					JOptionPane.showMessageDialog(null, "This soldier cannot move to this location.", "Cannot move", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				for (int i = 0;i < BOARD_SIZE;i++)
					for (int j = 0;j < BOARD_SIZE;j++)
						_buttons[i][j].setBackground(_buttons[i][j].getBackgroundColor());
				_possibleRoute.clear();
				
				_buttons[_selectedSoldierPosition[0]][_selectedSoldierPosition[1]].setImage(null, true);
				if (_myColor.equals("white"))
				{
					if (_i == 0 && _selectedSoldierName.equals("soldier-" + _myColor))
						_selectedSoldierName = "queen-" + _myColor;
				}
				else
				{
					if (_i == BOARD_SIZE - 1 && _selectedSoldierName.equals("soldier-" + _myColor))
						_selectedSoldierName = "queen-" + _myColor;
				}
				_buttons[_i][_j].setImage(_selectedSoldierName, true);
				
				final String image = _selectedSoldierName;
				final int[][] moves = new int[3][2];
				moves[0][0] = _selectedSoldierPosition[0];
				moves[0][1] = _selectedSoldierPosition[1];
				moves[1][0] = _i;
				moves[1][1] = _j;
				final int[] eatIndex = getEatIndex(_i, _j);
				if (eatIndex != null)
				{
					_buttons[eatIndex[0]][eatIndex[1]].setImage(null, true);
					
					moves[2][0] = eatIndex[0];
					moves[2][1] = eatIndex[1];
				}
				else
				{
					moves[2][0] = -1;
					moves[2][1] = -1;
				}
				
				_selectedSoldierName = null;
				_selectedSoldierPosition = null;
				_possibleEats.clear();
				_myTurn = false;
				
				Client.getInstance().sendPacket(new RequestTurnChange(image, moves));
			}
		}
	}
	
	public static CheckersScreen getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CheckersScreen INSTANCE = new CheckersScreen();
	}
}