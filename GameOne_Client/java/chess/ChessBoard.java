package chess;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.Client;
import network.request.RequestTurnChange;
import network.request.RequestUpdateGameScore;
import objects.GameResult;

/**
 * Chess board.
 * @author Sahar
 */
public final class ChessBoard extends JPanel
{
	private static final long serialVersionUID = 1436714889444297854L;
	
	private enum CheckStatus
	{
		NOT_UNDER_CHECK,
		UNDER_CHECK,
		UNDER_CHECKMATE
	}
	
	private static final int BOARD_SIZE = 8;
	private static final String[][] BOARD_SOLDIERS = new String[BOARD_SIZE][BOARD_SIZE];
	static
	{
		BOARD_SOLDIERS[0][4] = "king-black";
		BOARD_SOLDIERS[7][4] = "king-white";
		BOARD_SOLDIERS[0][3] = "queen-black";
		BOARD_SOLDIERS[7][3] = "queen-white";
		BOARD_SOLDIERS[0][2] = "bishop-black";
		BOARD_SOLDIERS[0][5] = "bishop-black";
		BOARD_SOLDIERS[7][2] = "bishop-white";
		BOARD_SOLDIERS[7][5] = "bishop-white";
		BOARD_SOLDIERS[0][1] = "knight-black";
		BOARD_SOLDIERS[0][6] = "knight-black";
		BOARD_SOLDIERS[7][1] = "knight-white";
		BOARD_SOLDIERS[7][6] = "knight-white";
		BOARD_SOLDIERS[0][0] = "rook-black";
		BOARD_SOLDIERS[0][7] = "rook-black";
		BOARD_SOLDIERS[7][0] = "rook-white";
		BOARD_SOLDIERS[7][7] = "rook-white";
		
		for (int i = 0;i < BOARD_SIZE;i++)
		{
			BOARD_SOLDIERS[1][i] = "pawn-black";
			BOARD_SOLDIERS[6][i] = "pawn-white";
		}
	}
	
	private final ChessButton[][] _buttons = new ChessButton[BOARD_SIZE][BOARD_SIZE];
	
	private List<int[]> _possibleRoute;
	private int[][] _enemyRoute;
	
	private String _myColor;
	private boolean _myTurn;
	
	private String _selectedSoldierName;
	private int[] _selectedSoldierPosition;
	
	private CheckStatus _check = CheckStatus.NOT_UNDER_CHECK;
	
	// Special root move.
	private boolean _canCast = true;
	// Special pawn move.
	private int[] _inPassing = {-1, -1};
	
	public ChessBoard(final String myColor)
	{
		_myColor = myColor;
		_myTurn = myColor.equals("white");
		
		setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
		
		boolean turn = false;
		if (_myTurn)
		{
			for (int i = 0;i < BOARD_SIZE;i++)
			{
				for (int j = 0;j < BOARD_SIZE;j++)
				{
					_buttons[i][j] = new ChessButton(BOARD_SOLDIERS[i][j], turn);
					_buttons[i][j].addMouseListener(new CellClick(i, j));
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
					_buttons[i][j] = new ChessButton(BOARD_SOLDIERS[i][j], turn);
					_buttons[i][j].addMouseListener(new CellClick(i, j));
					add(_buttons[i][j]);
					
					turn = !turn;
				}
				
				turn = !turn;
			}
		}
	}
	
	public void changeTurnAfterPromotion(final String image, final int oldX, final int oldY, final int newX, final int newY)
	{
		_buttons[newX][newY].setImage(image, true);
		_myTurn = false;
		
		final String[] images = new String[]
		{
			image,
			null
		};
		final int[][] positions = new int[][]
		{
			{
				oldX,
				oldY,
				newX,
				newY
			},
			{
				-1,
				-1,
				-1,
				-1
			}
		};
		
		Client.getInstance().sendPacket(new RequestTurnChange(images, positions));
	}
	
	public void updateData(final String[] images, final int[][] moves)
	{
		for (int i = 0;i < moves.length;i++)
		{
			if (i == 1 && images[1].isEmpty())
				break;
			
			if (images[i].equals("inPassing_make"))
			{
				_inPassing[0] = moves[i][2];
				_inPassing[1] = moves[i][3];
			}
			else if (images[i].equals("inPassing_kill"))
			{
				_buttons[moves[i][0]][moves[i][1]].setBackground(Color.PINK);
				_buttons[moves[i][2]][moves[i][3]].setBackground(Color.MAGENTA);
				_buttons[moves[i][2]][moves[i][3]].setImage(null, true);
			}
			else
			{
				_buttons[moves[i][0]][moves[i][1]].setBackground(Color.PINK);
				_buttons[moves[i][2]][moves[i][3]].setBackground(Color.MAGENTA);
				_buttons[moves[i][0]][moves[i][1]].setImage(null, true);
				_buttons[moves[i][2]][moves[i][3]].setImage(images[i], true);
			}
		}
		
		Toolkit.getDefaultToolkit().beep();
		
		_myTurn = true;
		_check = isUnderCheck();
		if (_check == CheckStatus.NOT_UNDER_CHECK)
			checkForTie();
	}
	
	public int calcScore()
	{
		return Math.max(getScoreOfColor("black"), getScoreOfColor("white"));
	}
	
	private List<int[]> movementHandler(int i, int j, final boolean withKing)
	{
		List<int[]> path = new ArrayList<>();
		if (_buttons[i][j].getName().equals("king"))
		{
			final String color = _buttons[i][j].getColor();
			_buttons[i][j].setImage(null, false);
			
			String obj;
			for (int x = i - 1;x <= i + 1;x++)
			{
				for (int y = j - 1;y <= j + 1;y++)
				{
					if (x == i && y == j)
						continue;
					if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE)
						continue;
					if (isTakenBy(color, x, y))
						continue;
					
					boolean found = true;
					if (isTakenBy(color.equals("white") ? "black" : "white", x, y))
					{
						obj = _buttons[x][y].getFullName();
						
						_buttons[x][y].setImage(null, false);
						if (!canBeEatenAt(x, y))
							found = false;
						_buttons[x][y].setImage(obj, true);
					}
					else if (withKing || !canBeEatenAt(x, y))
						found = false;
					if (found)
						continue;
					
					if (_enemyRoute == null)
						path.add(new int[] {x, y});
					else
					{
						for (int k = 0;k < _enemyRoute.length && !found;k++)
							if (_enemyRoute[k][0] == x & _enemyRoute[k][1] == y)
								found = true;
						
						if (!found)
							path.add(new int[] {x, y});
					}
				}
			}
			
			_buttons[i][j].setImage("king-" + color, true);
			if (color.equals(_myColor) && _canCast)
				path = checkCast(path);
		}
		else if (_buttons[i][j].getName().equals("queen"))
		{
			// horizontal
			for (int x = i + 1;x < BOARD_SIZE;x++)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, j))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, j))
				{
					path.add(new int[] {x, j});
					break;
				}
				
				path.add(new int[] {x, j});
			}
			for (int x = i - 1;x >= 0;x--)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, j))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, j))
				{
					path.add(new int[] {x, j});
					break;
				}
				
				path.add(new int[] {x, j});
			}
			// vertical
			for (int y = j + 1;y < BOARD_SIZE;y++)
			{
				if (isTakenBy(_buttons[i][j].getColor(), i, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", i, y))
				{
					path.add(new int[] {i, y});
					break;
				}
				
				path.add(new int[] {i, y});
			}
			for (int y = j - 1;y >= 0;y--)
			{
				if (isTakenBy(_buttons[i][j].getColor(), i, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", i, y))
				{
					path.add(new int[] {i, y});
					break;
				}
				
				path.add(new int[] {i, y});
			}
			// main diagonal
			for (int x = i + 1, y = j + 1;x < BOARD_SIZE && y < BOARD_SIZE;x++, y++)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					path.add(new int[] {x, y});
					break;
				}
				
				path.add(new int[] {x, y});
			}
			for (int x = i - 1, y = j - 1;x >= 0 && y >= 0;x--, y--)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					path.add(new int[] {x, y});
					break;
				}
				
				path.add(new int[] {x, y});
			}
			// sub diagonal
			for (int x = i + 1, y = j - 1;x < BOARD_SIZE && y >= 0;x++, y--)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					path.add(new int[] {x, y});
					break;
				}
				
				path.add(new int[] {x, y});
			}
			for (int x = i - 1, y = j + 1;x >= 0 && y < BOARD_SIZE;x--, y++)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					path.add(new int[] {x, y});
					break;
				}
				
				path.add(new int[] {x, y});
			}
		}
		else if (_buttons[i][j].getName().equals("bishop"))
		{
			// main diagonal
			for (int x = i + 1, y = j + 1;x < BOARD_SIZE && y < BOARD_SIZE;x++, y++)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					path.add(new int[] {x, y});
					break;
				}
				
				path.add(new int[] {x, y});
			}
			for (int x = i - 1, y = j - 1;x >= 0 && y >= 0;x--, y--)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					path.add(new int[] {x, y});
					break;
				}
				
				path.add(new int[] {x, y});
			}
			// sub diagonal
			for (int x = i + 1, y = j - 1;x < BOARD_SIZE && y >= 0;x++, y--)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					path.add(new int[] {x, y});
					break;
				}
				
				path.add(new int[] {x, y});
			}
			for (int x = i - 1, y = j + 1;x >= 0 && y < BOARD_SIZE;x--, y++)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, y))
				{
					path.add(new int[] {x, y});
					break;
				}
				
				path.add(new int[] {x, y});
			}
		}
		else if (_buttons[i][j].getName().equals("knight"))
		{
			i += 2;
			if (i < BOARD_SIZE)
			{
				if (j + 1 < BOARD_SIZE && !isTakenBy(_buttons[i - 2][j].getColor(), i, j + 1))
					path.add(new int[] {i, j + 1});
				if (j - 1 >= 0 && !isTakenBy(_buttons[i - 2][j].getColor(), i, j - 1))
					path.add(new int[] {i, j - 1});
			}
			i -= 4;
			if (i >= 0)
			{
				if (j + 1 < BOARD_SIZE && !isTakenBy(_buttons[i + 2][j].getColor(), i, j + 1))
					path.add(new int[] {i, j + 1});
				if (j - 1 >= 0 && !isTakenBy(_buttons[i + 2][j].getColor(), i, j - 1))
					path.add(new int[] {i, j - 1});
			}
			i += 2;
			j += 2;
			if (j < BOARD_SIZE)
			{
				if (i + 1 < BOARD_SIZE && !isTakenBy(_buttons[i][j - 2].getColor(), i + 1, j))
					path.add(new int[] {i + 1, j});
				if (i - 1 >= 0 && !isTakenBy(_buttons[i][j - 2].getColor(), i - 1, j))
					path.add(new int[] {i - 1, j});
			}
			j -= 4;
			if (j >= 0)
			{
				if (i + 1 < BOARD_SIZE && !isTakenBy(_buttons[i][j + 2].getColor(), i + 1, j))
					path.add(new int[] {i + 1, j});
				if (i - 1 >= 0 && !isTakenBy(_buttons[i][j + 2].getColor(), i - 1, j))
					path.add(new int[] {i - 1, j});
			}
		}
		else if (_buttons[i][j].getName().equals("rook"))
		{
			// horizontal
			for (int x = i + 1;x < BOARD_SIZE;x++)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, j))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, j))
				{
					path.add(new int[] {x, j});
					break;
				}
				
				path.add(new int[] {x, j});
			}
			for (int x = i - 1;x >= 0;x--)
			{
				if (isTakenBy(_buttons[i][j].getColor(), x, j))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", x, j))
				{
					path.add(new int[] {x, j});
					break;
				}
				
				path.add(new int[] {x, j});
			}
			// vertical
			for (int y = j + 1;y < BOARD_SIZE;y++)
			{
				if (isTakenBy(_buttons[i][j].getColor(), i, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", i, y))
				{
					path.add(new int[] {i, y});
					break;
				}
				
				path.add(new int[] {i, y});
			}
			for (int y = j - 1;y >= 0;y--)
			{
				if (isTakenBy(_buttons[i][j].getColor(), i, y))
					break;
				if (isTakenBy(_buttons[i][j].getColor().equals("white") ? "black" : "white", i, y))
				{
					path.add(new int[] {i, y});
					break;
				}
				
				path.add(new int[] {i, y});
			}
		}
		else
		{
			if (_buttons[i][j].getColor().equals("white"))
			{
				i--;
				
				if (_myColor.equals("white"))
				{
					if (i == 5)
					{
						for (int x = i;x > i - 2;x--)
						{
							if (_buttons[x][j].getName() != null)
								break;
							
							path.add(new int[] {x, j});
						}
					}
					else if (_buttons[i][j].getName() == null)
						path.add(new int[] {i, j});
					
					if (_enemyRoute == null)
					{
						if (j + 1 < BOARD_SIZE && isTakenBy("black", i, j + 1))
							path.add(new int[] {i, j + 1});
						if (j - 1 >= 0 && isTakenBy("black", i, j - 1))
							path.add(new int[] {i, j - 1});
					}
					
					if (_inPassing[0] == i && (_inPassing[1] == j - 1 || _inPassing[1] == j + 1))
						path.add(new int[] {_inPassing[0], _inPassing[1]});
				}
				else
				{
					if (_enemyRoute == null)
					{
						path.add(new int[] {i, j + 1});
						path.add(new int[] {i, j - 1});
					}
				}
			}
			else
			{
				i++;
				
				if (_myColor.equals("black"))
				{
					if (i == 2)
					{
						for (int x = i;x < i + 2;x++)
						{
							if (_buttons[x][j].getName() != null)
								break;
							
							path.add(new int[] {x, j});
						}
					}
					else if (_buttons[i][j].getName() == null)
						path.add(new int[] {i, j});
					
					if (_enemyRoute == null)
					{
						if (j + 1 < BOARD_SIZE && isTakenBy("white", i, j + 1))
							path.add(new int[] {i, j + 1});
						if (j - 1 >= 0 && isTakenBy("white", i, j - 1))
							path.add(new int[] {i, j - 1});
					}
					
					if (_inPassing[0] == i && (_inPassing[1] == j - 1 || _inPassing[1] == j + 1))
						path.add(new int[] {_inPassing[0], _inPassing[1]});
				}
				else
				{
					if (_enemyRoute == null)
					{
						path.add(new int[] {i, j + 1});
						path.add(new int[] {i, j - 1});
					}
				}
			}
		}
		
		return path;
	}
	
	private boolean isTakenBy(final String color, final int i, final int j)
	{
		return _buttons[i][j].getName() != null && _buttons[i][j].getColor().equals(color);
	}
	
	private List<int[]> checkCast(final List<int[]> path)
	{
		if (_myColor.equals("white"))
		{
			if (canBeEatenAt(7, 4))
				return path;
			
			if (_buttons[7][0].getFullName().equals("rook-white") && !_buttons[7][0].hasMoved() && _buttons[7][3].getName() == null && !canBeEatenAt(7, 3) && _buttons[7][2].getName() == null && !canBeEatenAt(7, 2) && _buttons[7][1].getName() == null && !canBeEatenAt(7, 1))
				path.add(new int[] {7, 2});
			if (_buttons[7][7].getFullName().equals("rook-white") && !_buttons[7][7].hasMoved() && _buttons[7][5].getName() == null && !canBeEatenAt(7, 5) && _buttons[7][6].getName() == null && !canBeEatenAt(7, 6))
				path.add(new int[] {7, 6});
		}
		else
		{
			if (canBeEatenAt(0, 4))
				return path;
			
			if (_buttons[0][0].getFullName().equals("rook-black") && !_buttons[0][0].hasMoved() && _buttons[0][3].getName() == null && !canBeEatenAt(0, 3) && _buttons[0][2].getName() == null && !canBeEatenAt(0, 2) && _buttons[0][1].getName() == null && !canBeEatenAt(0, 1))
				path.add(new int[] {0, 2});
			if (_buttons[0][7].getFullName().equals("rook-black") && !_buttons[0][7].hasMoved() && _buttons[0][5].getName() == null && !canBeEatenAt(0, 5) && _buttons[0][6].getName() == null && !canBeEatenAt(0, 6))
				path.add(new int[] {0, 6});
		}
		
		return path;
	}
	
	private boolean canMove(final int i, final int j)
	{
		for (final int[] route : _possibleRoute)
			if (route[0] == i && route[1] == j)
				return true;
		
		return false;
	}
	
	private CheckStatus isUnderCheck()
	{
		for (int i = 0;i < BOARD_SIZE;i++)
		{
			for (int j = 0;j < BOARD_SIZE;j++)
			{
				if (_buttons[i][j].getName() != null && _buttons[i][j].getFullName().equals("king-" + _myColor))
				{
					if (!canBeEatenAt(i, j))
						return CheckStatus.NOT_UNDER_CHECK;
					
					if (movementHandler(i, j, false).size() > 0)
					{
						JOptionPane.showMessageDialog(null, "You are under a check.", "CHECK", JOptionPane.INFORMATION_MESSAGE);
						return CheckStatus.UNDER_CHECK;
					}
					
					_enemyRoute = getThreateningSoldier(i, j);
					_myColor = _myColor.equals("white") ? "black" : "white";
					boolean found = false;
					for (i = 0;i < _enemyRoute.length && !found;i++)
						if (canBeEatenAt(_enemyRoute[i][0], _enemyRoute[i][1]))
							found = true;
					_myColor = _myColor.equals("white") ? "black" : "white";
					_enemyRoute = null;
					
					if (found)
					{
						JOptionPane.showMessageDialog(null, "You are under a check.", "CHECK", JOptionPane.INFORMATION_MESSAGE);
						return CheckStatus.UNDER_CHECK;
					}
					
					Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LOSE, calcScore()));
					
					JOptionPane.showMessageDialog(null, "You are under a checkmate, you lost!", "CHECKMATE", JOptionPane.INFORMATION_MESSAGE);
					
					return CheckStatus.UNDER_CHECKMATE;
				}
			}
		}
		
		return CheckStatus.NOT_UNDER_CHECK;
	}
	
	private int getScoreOfColor(final String color)
	{
		float score = 0;
		for (int i = 0;i < BOARD_SIZE;i++)
		{
			for (int j = 0;j < BOARD_SIZE;j++)
			{
				final String name = _buttons[i][j].getName();
				if (name == null)
					continue;
				if (!_buttons[i][j].getColor().equals(color))
					continue;
				
				if (name.equals("queen"))
					score += 5;
				else if (name.equals("rook"))
					score += 2;
				else if (name.equals("bishop"))
					score += 1.5f;
				else if (name.equals("knight"))
					score += 1;
				else if (name.equals("pawn"))
					score += 0.125f;
			}
		}
		
		return (int) score;
	}
	
	private void checkForTie()
	{
		for (int i = 0;i < BOARD_SIZE;i++)
			for (int j = 0;j < BOARD_SIZE;j++)
				if (_buttons[i][j].getName() != null && _buttons[i][j].getColor().equals(_myColor) && movementHandler(i, j, false).size() != 0)
					return;
		
		Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.TIE, 0));
		
		JOptionPane.showMessageDialog(null, "You are not under a check, but you cannot make any move.", "Tie", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private int[][] getThreateningSoldier(final int i, final int j)
	{
		for (int x = 0;x < BOARD_SIZE;x++)
		{
			for (int y = 0;y < BOARD_SIZE;y++)
			{
				if (_buttons[x][y].getName() == null)
					continue;
				if (_buttons[x][y].getColor().equals(_buttons[i][j].getColor()))
					continue;
				
				if (canSeeTarget(x, y, i, j))
				{
					if (_buttons[x][y].getName().equals("knight"))
						return new int[][] {{x, y}};
					
					int[][] ret = null;
					if (j == y)
					{
						if (i > x)
						{
							ret = new int[i - x][2];
							for (int z = i - 1, w = 0;z >= x;z--,w++)
							{
								ret[w][0] = z;
								ret[w][1] = y;
							}
						}
						else
						{
							ret = new int[x - i][2];
							for (int z = i + 1, w = 0;z <= x;z++,w++)
							{
								ret[w][0] = z;
								ret[w][1] = y;
							}
						}
					}
					else if (i == x)
					{
						if (j > y)
						{
							ret = new int[j - y][2];
							for (int z = j - 1, w = 0;z >= y;z--,w++)
							{
								ret[w][0] = x;
								ret[w][1] = z;
							}
						}
						else
						{
							ret = new int[y - j][2];
							for (int z = j + 1, w = 0;z <= y;z++,w++)
							{
								ret[w][0] = x;
								ret[w][1] = z;
							}
						}
					}
					else if (x > i && y > j)
					{
						ret = new int[x - i][2];
						for (int z = i + 1, w = j + 1, r = 0;z <= x && w <= y;z++,w++,r++)
						{
							ret[r][0] = z;
							ret[r][1] = w;
						}
					}
					else if (x < i && y < j)
					{
						ret = new int[i - x][2];
						for (int z = i - 1, w = j - 1, r = 0;z >= x && w >= y;z--,w--,r++)
						{
							ret[r][0] = z;
							ret[r][1] = w;
						}
					}
					else if (x < i && y > j)
					{
						ret = new int[i - x][2];
						for (int z = i - 1, w = j + 1, r = 0;z >= x && w <= y;z--,w++,r++)
						{
							ret[r][0] = z;
							ret[r][1] = w;
						}
					}
					else
					{
						ret = new int[x - i][2];
						for (int z = i + 1, w = j - 1, r = 0;z <= x && w >= y;z++,w--,r++)
						{
							ret[r][0] = z;
							ret[r][1] = w;
						}
					}
					return ret;
				}
			}
		}
		
		return null;
	}
	
	private boolean canSeeTarget(final int objX, final int objY, final int targetX, final int targetY)
	{
		final List<int[]> temp = movementHandler(objX, objY, false);
		if (temp.size() == 0)
			return false;
		for (final int[] route : temp)
			if (route[0] == targetX && route[1] == targetY)
				return true;
		
		return false;
	}
	
	private boolean canBeEatenAt(final int i, final int j)
	{
		final String color = _buttons[i][j].getName() == null ? _myColor : _buttons[i][j].getColor();
		for (int x = 0;x < BOARD_SIZE;x++)
		{
			for (int y = 0;y < BOARD_SIZE;y++)
			{
				if (_buttons[x][y].getName() == null)
					continue;
				if (_buttons[x][y].getColor().equals(color))
					continue;
				
				final List<int[]> temp = movementHandler(x, y, true);
				if (temp.size() == 0)
					continue;
				for (final int[] route : temp)
					if (route[0] == i && route[1] == j)
						return true;
				
				temp.clear();
			}
		}
		
		return false;
	}
	
	private class CellClick extends MouseAdapter
	{
		private int _i;
		private int _j;
		
		private CellClick(final int i, final int j)
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
				
				_possibleRoute = movementHandler(_i, _j, false);
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
				
				final String image = _selectedSoldierName;
				final int[] xy = _selectedSoldierPosition;
				_selectedSoldierName = null;
				_selectedSoldierPosition = null;
				
				final String tempTarget = _buttons[_i][_j].getFullName();
				
				_buttons[xy[0]][xy[1]].setImage(null, true);
				_buttons[_i][_j].setImage(image, true);
				
				_check = isUnderCheck();
				if (_check == CheckStatus.UNDER_CHECK)
				{
					_buttons[_i][_j].setImage(tempTarget, true);
					_buttons[xy[0]][xy[1]].setImage(image, true);
					return;
				}
				
				final String[] images = new String[2];
				final int[][] moves = new int[2][4];
				if (image.startsWith("rook"))
					_buttons[xy[0]][xy[1]].setMoved();
				else if (image.startsWith("king"))
				{
					if (_canCast)
					{
						if (_check == CheckStatus.NOT_UNDER_CHECK)
						{
							if (_j == 6)
							{
								images[0] = _buttons[_i][7].getFullName();
								
								moves[0][0] = _i;
								moves[0][1] = 7;
								moves[0][2] = _i;
								moves[0][3] = 5;
								
								_buttons[_i][5].setImage(_buttons[_i][7].getFullName(), true);
								_buttons[_i][7].setImage(null, true);
							}
							else if (_j == 2)
							{
								images[0] = _buttons[_i][0].getFullName();
								
								moves[0][0] = _i;
								moves[0][1] = 0;
								moves[0][2] = _i;
								moves[0][3] = 3;
								
								_buttons[_i][3].setImage(_buttons[_i][0].getFullName(), true);
								_buttons[_i][0].setImage(null, true);
							}
						}
						
						_canCast = false;
					}
				}
				else if (image.startsWith("pawn"))
				{
					if (_myColor.equals("white"))
					{
						if (_i == 0)
						{
							ChessScreen.getInstance().getPromotionPanel().showSelectionWindow(_myColor, xy[0], xy[1], _i, _j);
							
							_inPassing[0] = _inPassing[1] = -1;
							return;
						}
						
						if (_i == xy[0] - 2 && (_j - 1 >= 0 && _buttons[_i][_j - 1].getName() != null && _buttons[_i][_j - 1].getFullName().equals("pawn-black") || _j + 1 < BOARD_SIZE && _buttons[_i][_j + 1].getName() != null && _buttons[_i][_j + 1].getFullName().equals("pawn-black")))
						{
							images[0] = "inPassing_make";
							
							moves[0][0] = -1;
							moves[0][1] = -1;
							moves[0][2] = _i + 1;
							moves[0][3] = _j;
						}
						else if (_inPassing[0] != -1 && _inPassing[1] != -1 && _inPassing[0] == _i && _inPassing[1] == _j)
						{
							_buttons[_i + 1][_j].setImage(null, true);
							
							images[0] = "inPassing_kill";
							
							moves[0][0] = xy[0];
							moves[0][1] = xy[1];
							moves[0][2] = _i + 1;
							moves[0][3] = _j;
						}
					}
					else
					{
						if (_i == BOARD_SIZE - 1)
						{
							ChessScreen.getInstance().getPromotionPanel().showSelectionWindow(_myColor, xy[0], xy[1], _i, _j);
							
							_inPassing[0] = _inPassing[1] = -1;
							return;
						}
						
						if (_i == xy[0] + 2 && (_j - 1 >= 0 && _buttons[_i][_j - 1].getName() != null && _buttons[_i][_j - 1].getFullName().equals("pawn-white") || _j + 1 < BOARD_SIZE && _buttons[_i][_j + 1].getName() != null && _buttons[_i][_j + 1].getFullName().equals("pawn-white")))
						{
							images[0] = "inPassing_make";
							
							moves[0][0] = -1;
							moves[0][1] = -1;
							moves[0][2] = _i - 1;
							moves[0][3] = _j;
						}
						else if (_inPassing[0] != -1 && _inPassing[1] != -1 && _inPassing[0] == _i && _inPassing[1] == _j)
						{
							_buttons[_i - 1][_j].setImage(null, true);
							
							images[0] = "inPassing_kill";
							
							moves[0][0] = xy[0];
							moves[0][1] = xy[1];
							moves[0][2] = _i - 1;
							moves[0][3] = _j;
						}
					}
				}
				
				if (images[0] == null)
				{
					images[0] = image;
					moves[0][0] = xy[0];
					moves[0][1] = xy[1];
					moves[0][2] = _i;
					moves[0][3] = _j;
					moves[1] = new int[] {-1, -1, -1, -1};
				}
				else
				{
					images[1] = image;
					moves[1][0] = xy[0];
					moves[1][1] = xy[1];
					moves[1][2] = _i;
					moves[1][3] = _j;
				}
				
				_inPassing[0] = _inPassing[1] = -1;
				_myTurn = false;
				
				Client.getInstance().sendPacket(new RequestTurnChange(images, moves));
			}
		}
	}
}