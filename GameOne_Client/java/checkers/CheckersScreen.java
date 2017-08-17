package checkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
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
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import client.Client;
import configs.Config;
import configs.GameConfig;
import network.request.RequestTurnChange;
import network.request.RequestUpdateGameScore;
import network.request.RequestWaitingRoom;
import objects.GameId;
import objects.GameResult;
import util.Direction;
import windows.WaitingRoom;

public final class CheckersScreen extends JFrame
{
	private static final long serialVersionUID = -4162982244689099156L;
	private static final Logger LOGGER = Logger.getLogger(CheckersScreen.class.getName());
	private static final String IMAGE_PATH = "./images/checkers/";
	
	public static final int BOARD_SIZE = 8;
	public static final Map<String, Image> IMAGES = new HashMap<>();
	static
	{
		for (final File file : new File(IMAGE_PATH).listFiles())
			if (file.isFile())
				IMAGES.put(file.getName().substring(0, file.getName().lastIndexOf('.')), new ImageIcon(file.getAbsolutePath()).getImage());
	}
	
	protected final CheckersCell[][] _buttons = new CheckersCell[BOARD_SIZE][BOARD_SIZE];
	protected final List<CheckersCell> _possibleRoute = new ArrayList<>();
	protected final Map<CheckersCell, CheckersCell> _possibleEats = new HashMap<>();
	
	protected String _myColor;
	protected boolean _myTurn;
	protected CheckersCell _selectedSoldier;
	
	protected CheckersScreen()
	{
		super("GameOne Client - Checkers");
		
		setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
		setMinimumSize(new Dimension(600, 600));
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
		
		final CellClick ml = new CellClick();
		if (_myTurn)
		{
			for (int i = 0;i < BOARD_SIZE;i++)
			{
				for (int j = 0;j < BOARD_SIZE;j++)
				{
					// White blocks.
					if (turn)
						_buttons[i][j] = new CheckersCell("", i, j, turn);
					// Empty blocks.
					else if (i == 3 || i == 4)
					{
						_buttons[i][j] = new CheckersCell("", i, j, turn);
						_buttons[i][j].addMouseListener(ml);
					}
					// Blocks with players.
					else
					{
						if (i < 3)
							_buttons[i][j] = new CheckersCell("soldier-black", i, j, turn);
						else
							_buttons[i][j] = new CheckersCell("soldier-white", i, j, turn);
						
						_buttons[i][j].addMouseListener(ml);
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
						_buttons[i][j] = new CheckersCell("", i, j, turn);
					// Empty blocks.
					else if (i == 3 || i == 4)
					{
						_buttons[i][j] = new CheckersCell("", i, j, turn);
						_buttons[i][j].addMouseListener(ml);
					}
					// Blocks with players.
					else
					{
						if (i < 3)
							_buttons[i][j] = new CheckersCell("soldier-black", i, j, turn);
						else
							_buttons[i][j] = new CheckersCell("soldier-white", i, j, turn);
						
						_buttons[i][j].addMouseListener(ml);
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
		setExtendedState(Frame.MAXIMIZED_BOTH);
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
		if (GameConfig.CHECKERS_PAINT_MOVES)
			_buttons[moves[0][0]][moves[0][1]].setBackground(Color.PINK);
		_buttons[moves[0][0]][moves[0][1]].setImage("", true);
		if (!image.isEmpty())
		{
			if (GameConfig.CHECKERS_PAINT_MOVES)
				_buttons[moves[1][0]][moves[1][1]].setBackground(Color.MAGENTA);
			_buttons[moves[1][0]][moves[1][1]].setImage(image, true);
			
			if (moves[2][0] != -1)
			{
				if (GameConfig.CHECKERS_PAINT_MOVES)
					_buttons[moves[2][0]][moves[2][1]].setBackground(Color.RED);
				_buttons[moves[2][0]][moves[2][1]].setImage("", true);
				
				if (GameConfig.BURN_PLAYERS)
				{
					// Change myColor to enemy color temporary.
					_myColor = _myColor.equals("white") ? "black" : "white";
					// Check if the player can eat anything else in the spot it moved to.
					movementHandler(moves[1][0], moves[1][1]);
					// Nothing to eat, its my turn.
					if (_possibleEats.isEmpty())
						setTurn();
					// Reset back variables.
					_myColor = _myColor.equals("white") ? "black" : "white";
					_possibleEats.clear();
					_possibleRoute.clear();
				}
				else
					setTurn();
			}
			else
				setTurn();
		}
		else
			setTurn();
		
		if (lost())
		{
			Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LOSE, calcScore()));
			
			JOptionPane.showMessageDialog(null, "You are out of soldiers.", "You lost!", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	protected void movementHandler(final int i, final int j)
	{
		if (_buttons[i][j].getImage().startsWith("queen"))
		{
			if (GameConfig.QUEEN_SINGLE_STEP)
			{
				checkDirection(i, j, Direction.ABOVE);
				checkDirection(i, j, Direction.BELOW);
			}
			else
			{
				// Flag holding the first index for eat possibility.
				// Queen cannot eat more than one soldier at once.
				// As well as any index beyond the first eat index should be added to _possibleEats.
				CheckersCell engaged = null;
				
				// Search main diagonal.
				for (int x = i + 1, y = j + 1;x < BOARD_SIZE && y < BOARD_SIZE;x++, y++)
				{
					// If spot taken by self, break.
					if (_buttons[x][y].getImage().endsWith(_myColor))
						break;
					// If spot taken by enemy.
					if (hasEnemy(x, y))
					{
						if (engaged != null)
							break;
						
						// Allow to eat only if there's no other soldier behind it.
						if (canEat(x, y, Direction.BELOW, Direction.RIGHT))
						{
							_possibleEats.put(_buttons[x + 1][y + 1], _buttons[x][y]);
							_possibleRoute.add(_buttons[x + 1][y + 1]);
							
							engaged = _buttons[x][y];
							
							x++;
							y++;
						}
						else
							break;
					}
					// Spot empty.
					else
					{
						_possibleRoute.add(_buttons[x][y]);
						
						if (engaged != null)
							_possibleEats.put(_buttons[x][y], engaged);
					}
				}
				engaged = null;
				for (int x = i - 1, y = j - 1;x >= 0 && y >= 0;x--, y--)
				{
					// If spot taken by self, break.
					if (_buttons[x][y].getImage().endsWith(_myColor))
						break;
					// If spot taken by enemy.
					if (hasEnemy(x, y))
					{
						if (engaged != null)
							break;
						
						// Allow to eat only if there's no other soldier behind it.
						if (canEat(x, y, Direction.ABOVE, Direction.LEFT))
						{
							_possibleEats.put(_buttons[x - 1][y - 1], _buttons[x][y]);
							_possibleRoute.add(_buttons[x - 1][y - 1]);
							
							engaged = _buttons[x][y];
							
							x--;
							y--;
						}
						else
							break;
					}
					// Spot empty.
					else
					{
						_possibleRoute.add(_buttons[x][y]);
						
						if (engaged != null)
							_possibleEats.put(_buttons[x][y], engaged);
					}
				}
				
				engaged = null;
				
				// Search sub diagonal.
				for (int x = i + 1, y = j - 1;x < BOARD_SIZE && y >= 0;x++, y--)
				{
					// If spot taken by self, break.
					if (_buttons[x][y].getImage().endsWith(_myColor))
						break;
					// If spot taken by enemy.
					if (hasEnemy(x, y))
					{
						if (engaged != null)
							break;
						
						// Allow to eat only if there's no other soldier behind it.
						if (canEat(x, y, Direction.BELOW, Direction.LEFT))
						{
							_possibleEats.put(_buttons[x + 1][y - 1], _buttons[x][y]);
							_possibleRoute.add(_buttons[x + 1][y - 1]);
							
							engaged = _buttons[x][y];
							
							x++;
							y--;
						}
						else
							break;
					}
					// Spot empty.
					else
					{
						_possibleRoute.add(_buttons[x][y]);
						
						if (engaged != null)
							_possibleEats.put(_buttons[x][y], engaged);
					}
				}
				engaged = null;
				for (int x = i - 1, y = j + 1;x >= 0 && y < BOARD_SIZE;x--, y++)
				{
					// If spot taken by self, break.
					if (_buttons[x][y].getImage().endsWith(_myColor))
						break;
					// If spot taken by enemy.
					if (hasEnemy(x, y))
					{
						if (engaged != null)
							break;
						
						// Allow to eat only if there's no other soldier behind it.
						if (canEat(x, y, Direction.ABOVE, Direction.RIGHT))
						{
							_possibleEats.put(_buttons[x - 1][y + 1], _buttons[x][y]);
							_possibleRoute.add(_buttons[x - 1][y + 1]);
							
							engaged = _buttons[x][y];
							
							x--;
							y++;
						}
						else
							break;
					}
					// Spot empty.
					else
					{
						_possibleRoute.add(_buttons[x][y]);
						
						if (engaged != null)
							_possibleEats.put(_buttons[x][y], engaged);
					}
				}
			}
		}
		else
		{
			if (_buttons[i][j].getImage().endsWith("white"))
				checkDirection(i, j, Direction.ABOVE);
			else
				checkDirection(i, j, Direction.BELOW);
		}
	}
	
	protected boolean canMove(final CheckersCell target)
	{
		return _possibleRoute.contains(target);
	}
	
	protected CheckersCell getEatIndex(final CheckersCell source)
	{
		return _possibleEats.get(source);
	}
	
	protected void setTurn()
	{
		if (Config.GAME_BEEP)
			Toolkit.getDefaultToolkit().beep();
		
		_myTurn = true;
	}
	
	private boolean lost()
	{
		for (final CheckersCell[] cells : _buttons)
			for (final CheckersCell cell : cells)
				if (cell.getImage().endsWith(_myColor))
					return false;
		
		return true;
	}
	
	private int getScoreOfColor(final String color)
	{
		int score = 0;
		for (final CheckersCell[] cells : _buttons)
			for (final CheckersCell cell : cells)
				if (cell.getImage().endsWith(color))
					score++;
		
		return score;
	}
	
	private int calcScore()
	{
		return Math.max(getScoreOfColor("black"), getScoreOfColor("white"));
	}
	
	private boolean isEmptySpot(final int i, final int j)
	{
		return _buttons[i][j].getImage().isEmpty();
	}
	
	private boolean hasEnemy(final int i, final int j)
	{
		final String enemyColor = _myColor.equals("white") ? "black" : "white";
		return _buttons[i][j].getImage().endsWith(enemyColor);
	}
	
	private boolean canEat(int i, int j, final Direction verticalDirection, final Direction horizontalDirection)
	{
		if (!hasEnemy(i, j))
			return false;
		
		switch (verticalDirection)
		{
			case ABOVE:
				i--;
				break;
			case BELOW:
				i++;
				break;
		}
		switch (horizontalDirection)
		{
			case LEFT:
				j--;
				break;
			case RIGHT:
				j++;
				break;
		}
		
		if (!isLegitSpot(i, j))
			return false;
		
		return isEmptySpot(i, j);
	}
	
	private void checkDirection(final int i, final int j, final Direction verticalDirection)
	{
		final int x = verticalDirection == Direction.ABOVE ? i - 1 : i + 1;
		for (final Direction horizontalDirection : Direction.HORIZONTAL_DIRECTIONS)
		{
			final int y = horizontalDirection == Direction.LEFT ? j - 1 : j + 1;
			if (!isLegitSpot(x, y))
				continue;
			
			if (isEmptySpot(x, y))
				_possibleRoute.add(_buttons[x][y]);
			else if (canEat(x, y, verticalDirection, horizontalDirection))
			{
				final int[] goIndex = new int[] {verticalDirection == Direction.ABOVE ? x - 1 : x + 1, horizontalDirection == Direction.LEFT ? y - 1 : y + 1};
				_possibleEats.put(_buttons[goIndex[0]][goIndex[1]], _buttons[x][y]);
				_possibleRoute.add(_buttons[goIndex[0]][goIndex[1]]);
			}
		}
	}
	
	private static boolean isLegitSpot(final int i, final int j)
	{
		return i >= 0 && j >= 0 && i < BOARD_SIZE && j < BOARD_SIZE;
	}
	
	protected class CellClick extends MouseAdapter
	{
		@Override
		public void mousePressed(final MouseEvent e)
		{
			if (!_myTurn)
			{
				JOptionPane.showMessageDialog(null, "This is not your turn, please wait.", "Wait", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			final CheckersCell cell = (CheckersCell) e.getSource();
			if (_selectedSoldier == null)
			{
				if (cell.getImage().isEmpty())
				{
					JOptionPane.showMessageDialog(null, "Please select a soldier to move first.", "Select a soldier", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!cell.getImage().endsWith(_myColor))
				{
					JOptionPane.showMessageDialog(null, "This is not your soldier.", "Illegal action", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				movementHandler(cell.getI(), cell.getJ());
				if (_possibleRoute.size() == 0)
				{
					JOptionPane.showMessageDialog(null, "This soldier cannot move anywhere.", "Cannot move", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (GameConfig.CHECKERS_PAINT_ROUTE)
					for (final CheckersCell route : _possibleRoute)
						route.setBackground(Color.YELLOW);
				cell.setBackground(Color.ORANGE);
				
				_selectedSoldier = cell;
			}
			else
			{
				if (_selectedSoldier == cell)
				{
					if (GameConfig.CHECKERS_PAINT_ROUTE)
						for (final CheckersCell route : _possibleRoute)
							route.setBackground(route.getOriginalBackground());
					cell.setBackground(cell.getOriginalBackground());
					
					_selectedSoldier = null;
					_possibleRoute.clear();
					_possibleEats.clear();
					return;
				}
				if (!canMove(cell))
				{
					JOptionPane.showMessageDialog(null, "This soldier cannot move to this location.", "Cannot move", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				_myTurn = false;
				
				for (int i = 0;i < BOARD_SIZE;i++)
					for (int j = 0;j < BOARD_SIZE;j++)
						_buttons[i][j].setBackground(_buttons[i][j].getOriginalBackground());
				
				String soldierName = _selectedSoldier.getImage();
				
				_selectedSoldier.setImage("", true);
				if (_myColor.equals("white"))
				{
					if (cell.getI() == 0 && soldierName.equals("soldier-" + _myColor))
						soldierName = "queen-" + _myColor;
				}
				else
				{
					if (cell.getI() == BOARD_SIZE - 1 && soldierName.equals("soldier-" + _myColor))
						soldierName = "queen-" + _myColor;
				}
				
				final CheckersCell eatIndex = getEatIndex(cell);
				if (GameConfig.BURN_PLAYERS && eatIndex == null)
				{
					boolean burn = false;
					for (int i = 0;i < BOARD_SIZE && !burn;i++)
					{
						for (int j = 0;j < BOARD_SIZE && !burn;j++)
						{
							if (_buttons[i][j].getImage().endsWith(_myColor))
							{
								_possibleEats.clear();
								
								movementHandler(i, j);
								if (!_possibleEats.isEmpty())
									burn = true;
							}
						}
					}
					if (burn)
					{
						JOptionPane.showMessageDialog(null, "You must eat whenever possible and you didn't do so.", "Missed Eat", JOptionPane.INFORMATION_MESSAGE);
					
						soldierName = null;
					}
					else
						cell.setImage(soldierName, true);
				}
				else
					cell.setImage(soldierName, true);
				
				final String image = soldierName;
				final int[][] moves = new int[3][2];
				moves[0][0] = _selectedSoldier.getI();
				moves[0][1] = _selectedSoldier.getJ();
				moves[1][0] = cell.getI();
				moves[1][1] = cell.getJ();
				if (eatIndex != null)
				{
					eatIndex.setImage("", true);
					
					moves[2][0] = eatIndex.getI();
					moves[2][1] = eatIndex.getJ();
					
					_possibleEats.clear();
					
					movementHandler(cell.getI(), cell.getJ());
					if (!_possibleEats.isEmpty())
						setTurn();
				}
				else
				{
					moves[2][0] = -1;
					moves[2][1] = -1;
				}
				
				_selectedSoldier = null;
				_possibleEats.clear();
				_possibleRoute.clear();
				
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