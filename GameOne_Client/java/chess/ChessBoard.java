package chess;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import chess.objects.AbstractObject;
import chess.objects.Bishop;
import chess.objects.King;
import chess.objects.Knight;
import chess.objects.Pawn;
import chess.objects.Queen;
import chess.objects.Rook;
import client.Client;
import configs.Config;
import configs.GameConfig;
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
	private static final Logger LOGGER = Logger.getLogger(ChessBoard.class.getName());
	
	private static final String PACKAGE_PATH = "chess.objects.";
	private static final List<AbstractObject> SOLDIERS = new ArrayList<>();
	static
	{
		SOLDIERS.add(new Rook(0, 0, "black"));
		SOLDIERS.add(new Knight(0, 1, "black"));
		SOLDIERS.add(new Bishop(0, 2, "black"));
		SOLDIERS.add(new Queen(0, 3, "black"));
		SOLDIERS.add(new King(0, 4, "black"));
		SOLDIERS.add(new Bishop(0, 5, "black"));
		SOLDIERS.add(new Knight(0, 6, "black"));
		SOLDIERS.add(new Rook(0, 7, "black"));
		
		SOLDIERS.add(new Rook(7, 0, "white"));
		SOLDIERS.add(new Knight(7, 1, "white"));
		SOLDIERS.add(new Bishop(7, 2, "white"));
		SOLDIERS.add(new Queen(7, 3, "white"));
		SOLDIERS.add(new King(7, 4, "white"));
		SOLDIERS.add(new Bishop(7, 5, "white"));
		SOLDIERS.add(new Knight(7, 6, "white"));
		SOLDIERS.add(new Rook(7, 7, "white"));
		
		for (int i = 0;i < 8;i++)
		{
			SOLDIERS.add(new Pawn(1, i, "black"));
			SOLDIERS.add(new Pawn(6, i, "white"));
		}
	}
	
	private final List<AbstractObject> _allies = new CopyOnWriteArrayList<>();
	private final List<AbstractObject> _enemies = new CopyOnWriteArrayList<>();
	
	private final ChessCell[][] _cells = new ChessCell[8][8];
	private final int[] _inPassing = {-1, -1};
	private String _myColor;
	private King _myKing;
	private boolean _myTurn;
	private ChessCell _selectedCell;
	
	protected ChessBoard()
	{
		setLayout(new GridLayout(8, 8));
		
		boolean turn = false;
		for (int i = 0;i < 8;i++)
		{
			for (int j = 0;j < 8;j++)
			{
				_cells[i][j] = new ChessCell(i, j, turn);
				_cells[i][j].addActionListener(a -> cellClick(a));
				
				turn = !turn;
			}
			
			turn = !turn;
		}
		
		LOGGER.info("Chess board loaded!");
	}
	
	public void start(final String myColor)
	{
		// Set color and turn.
		_myColor = myColor;
		_myTurn = _myColor.equals("white");
		
		// Replace components.
		removeAll();
		if (_myColor.equals("white"))
			for (int i = 0;i < 8;i++)
				for (int j = 0;j < 8;j++)
					add(_cells[i][j]);
		else
			for (int i = 7;i >= 0;i--)
				for (int j = 7;j >= 0;j--)
					add(_cells[i][j]);
		revalidate();
		
		// Reset all cells.
		_allies.clear();
		_enemies.clear();
		for (final ChessCell[] cells : _cells)
		{
			for (final ChessCell cell : cells)
			{
				cell.setObject(null);
				cell.setBackground(cell.getBackgroundColor());
			}
		}
		for (final AbstractObject obj : SOLDIERS)
		{
			if (obj.getOwner().equals(_myColor))
			{
				_allies.add(obj);
				
				if (obj instanceof King)
					_myKing = (King) obj;
			}
			else
				_enemies.add(obj);
			
			obj.setMoved(false);
			
			_cells[obj.getInitialX()][obj.getInitialY()].setObject(obj);
		}
		
		// Build paths, enemies first.
		for (final AbstractObject obj : _enemies)
			obj.buildPaths(true);
		for (final AbstractObject obj : _allies)
			obj.buildPaths(false);
		// Validate paths for ally soldiers only.
		for (final AbstractObject obj : _allies)
			obj.validatePath();
	}
	
	public ChessCell[][] getCells()
	{
		return _cells;
	}
	
	public ChessCell getCell(final int x, final int y)
	{
		return _cells[x][y];
	}
	
	public ChessCell getCell(final AbstractObject object)
	{
		for (final ChessCell[] cells : _cells)
			for (final ChessCell cell : cells)
				if (cell.getObject() == object)
					return cell;
		
		return null;
	}
	
	public int[] getInPassing()
	{
		return _inPassing;
	}
	
	public King getMyKing()
	{
		return _myKing;
	}
	
	public List<ChessCell> getThreateningCells(final AbstractObject threatended)
	{
		final List<ChessCell> threatCells = new ArrayList<>();
		for (final ChessCell[] cells : _cells)
		{
			for (final ChessCell cell : cells)
			{
				// Cell not owned, skip.
				if (cell.getObject() == null)
					continue;
				// Cell owned by ally, skip.
				if (cell.getObject().isAlly(threatended.getOwner()))
					continue;
				
				if (cell.getObject().canEat(threatended))
					threatCells.add(cell);
			}
		}
		
		return threatCells;
	}
	
	public boolean canBeSeenBy(final String owner, final ChessCell targetCell)
	{
		for (final ChessCell[] cells : _cells)
		{
			for (final ChessCell cell : cells)
			{
				// Cell not owned, skip.
				if (cell.getObject() == null)
					continue;
				// Cell isn't owner, skip.
				if (!cell.getObject().isAlly(owner))
					continue;
				
				if (cell.getObject().canSee(targetCell))
					return true;
			}
		}
		
		return false;
	}
	
	public boolean canBeEaten(final AbstractObject object)
	{
		return !getThreateningCells(object).isEmpty();
	}
	
	public boolean canBeBlocked(final King king, final List<ChessCell> pathToKing)
	{
		for (final ChessCell[] cells : _cells)
		{
			for (final ChessCell cell : cells)
			{
				// Cell not owned, skip.
				if (cell.getObject() == null)
					continue;
				
				// Cell owned by ally, check if can block.
				if (cell.getObject().isAlly(king.getOwner()) && cell.getObject().canBlock(pathToKing))
					return true;
			}
		}
		
		return false;
	}
	
	public void changeTurnAfterPromotion(final String className, final int oldX, final int oldY, final int newX, final int newY)
	{
		try
		{
			final AbstractObject newObj = (AbstractObject) Class.forName(PACKAGE_PATH + className).getConstructors()[0].newInstance(0, 0, _myColor);
			newObj.setMoved(true);
			
			_allies.remove(_cells[newX][newY].getObject());
			_allies.add(newObj);
			
			_cells[newX][newY].setObject(newObj);
			
			_selectedCell = null;
			_inPassing[0] = _inPassing[1] = -1;
			_myTurn = false;
			
			final String[] images = new String[]
			{
				className,
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
		catch (final SecurityException | ClassNotFoundException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			LOGGER.log(Level.WARNING, "Failed creating new chess object out of promotion: ", e);
		}
	}
	
	public void updateData(final String[] images, final int[][] moves)
	{
		for (int i = 0;i < moves.length;i++)
		{
			if (images[i].isEmpty())
				break;
			
			if (images[i].equals("inPassing_make"))
			{
				_inPassing[0] = moves[i][2];
				_inPassing[1] = moves[i][3];
			}
			else if (images[i].equals("inPassing_kill"))
			{
				if (GameConfig.CHESS_PAINT_MOVES)
				{
					_cells[moves[i][0]][moves[i][1]].setBackground(Color.PINK);
					_cells[moves[i][2]][moves[i][3]].setBackground(Color.MAGENTA);
				}
				
				_allies.remove(_cells[moves[i][2]][moves[i][3]]);
				
				_cells[moves[i][2]][moves[i][3]].setObject(null);
			}
			else
			{
				if (GameConfig.CHESS_PAINT_MOVES)
				{
					_cells[moves[i][0]][moves[i][1]].setBackground(Color.PINK);
					_cells[moves[i][2]][moves[i][3]].setBackground(Color.MAGENTA);
				}
				
				final AbstractObject moved = _cells[moves[i][0]][moves[i][1]].getObject();
				_cells[moves[i][0]][moves[i][1]].setObject(null);
				
				if (_cells[moves[i][2]][moves[i][3]].getObject() != null)
					_allies.remove(_cells[moves[i][2]][moves[i][3]].getObject());
				
				if (moved.getClass().getSimpleName().equals(images[i]))
				{
					_cells[moves[i][2]][moves[i][3]].setObject(moved);
					
					moved.setMoved(true);
				}
				else
				{
					// It was promoted.
					try
					{
						final AbstractObject newObj = (AbstractObject) Class.forName(PACKAGE_PATH + images[i]).getConstructors()[0].newInstance(0, 0, moved.getOwner());
						newObj.setMoved(true);
						
						_enemies.remove(moved);
						_enemies.add(newObj);
						
						_cells[moves[i][2]][moves[i][3]].setObject(newObj);
					}
					catch (final SecurityException | ClassNotFoundException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e)
					{
						LOGGER.log(Level.WARNING, "Failed creating new chess object out of promotion: ", e);
					}
				}
			}
		}
		
		// Build paths, enemies first.
		for (final AbstractObject obj : _enemies)
			obj.buildPaths(true);
		for (final AbstractObject obj : _allies)
			obj.buildPaths(false);
		// Validate paths for ally soldiers only.
		for (final AbstractObject obj : _allies)
			obj.validatePath();
		
		if (Config.GAME_BEEP)
			Toolkit.getDefaultToolkit().beep();
		
		_myTurn = true;
		
		final CheckStatus check = _myKing.getCheckStatus();
		switch (check)
		{
			case NOT_UNDER_CHECK:
				checkForTie();
				break;
			case UNDER_CHECK:
				JOptionPane.showMessageDialog(null, "You are under a check.", "Check", JOptionPane.INFORMATION_MESSAGE);
				break;
			case UNDER_CHECKMATE:
				Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LOSE, calcScore()));
				ChessBackground.getInstance().showDialog("Checkmate", ChessBackground.LOST);
				break;
		}
	}
	
	public int calcScore()
	{
		return Math.max(getScoreOfColor("black"), getScoreOfColor("white"));
	}
	
	private int getScoreOfColor(final String color)
	{
		int score = 0;
		for (final ChessCell[] cells : _cells)
			for (final ChessCell cell : cells)
				if (cell.getObject() != null && cell.getObject().getOwner().equals(color))
					score += cell.getObject().getScore();
		
		return score;
	}
	
	private void checkForTie()
	{
		for (final ChessCell[] cells : _cells)
			for (final ChessCell cell : cells)
				if (cell.getObject() != null && cell.getObject().getOwner().equals(_myColor) && !cell.getObject().getPathToShow().isEmpty())
					return;
		
		Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.TIE, 0));
		ChessBackground.getInstance().showDialog("Tie", ChessBackground.TIE);
	}
	
	private void cellClick(final ActionEvent e)
	{
		if (!_myTurn)
		{
			JOptionPane.showMessageDialog(null, "This is not your turn, please wait.", "Wait", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		final ChessCell cell = (ChessCell) e.getSource();
		if (_selectedCell == null)
		{
			if (cell.getObject() == null)
			{
				JOptionPane.showMessageDialog(null, "Please select a soldier to move first.", "Select a soldier", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!cell.getObject().getOwner().equals(_myColor))
			{
				JOptionPane.showMessageDialog(null, "This is not your soldier.", "Illegal action", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			final List<ChessCell> route = cell.getObject().getPathToShow();
			if (route.isEmpty())
			{
				JOptionPane.showMessageDialog(null, "This soldier cannot move anywhere.", "Cannot move", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (GameConfig.CHESS_PAINT_ROUTE)
				for (final ChessCell routeCell : route)
					routeCell.setBackground(Color.YELLOW);
			cell.setBackground(Color.ORANGE);
			
			_selectedCell = cell;
		}
		else if (_selectedCell == cell)
		{
			if (GameConfig.CHESS_PAINT_ROUTE)
				for (final ChessCell routeCell : cell.getObject().getPathToShow())
					routeCell.setBackground(routeCell.getBackgroundColor());
			cell.setBackground(cell.getBackgroundColor());
			
			_selectedCell = null;
		}
		else
		{
			final List<ChessCell> route = _selectedCell.getObject().getPathToShow();
			if (!route.contains(cell))
			{
				JOptionPane.showMessageDialog(null, "This soldier cannot move to this location.", "Cannot move", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			final AbstractObject selectedObject = _selectedCell.getObject();
			_selectedCell.setObject(null);
			
			if (cell.getObject() != null)
				_enemies.remove(cell.getObject());
			
			cell.setObject(selectedObject);
			
			for (final ChessCell[] cells : _cells)
				for (final ChessCell c : cells)
					c.setBackground(c.getBackgroundColor());
			
			final String[] images = new String[2];
			final int[][] moves = new int[2][4];
			if (selectedObject instanceof King)
			{
				// Castling.
				if (!selectedObject.hasMoved())
				{
					if (cell.getCellY() == 6)
					{
						images[0] = _cells[cell.getCellX()][7].getObject().getClass().getSimpleName();
						
						moves[0][0] = cell.getCellX();
						moves[0][1] = 7;
						moves[0][2] = cell.getCellX();
						moves[0][3] = 5;
						
						_cells[cell.getCellX()][5].setObject(_cells[cell.getCellX()][7].getObject());
						_cells[cell.getCellX()][7].setObject(null);
						
						_cells[cell.getCellX()][5].getObject().setMoved(true);
					}
					else if (cell.getCellY() == 2)
					{
						images[0] = _cells[cell.getCellX()][0].getObject().getClass().getSimpleName();
						
						moves[0][0] = cell.getCellX();
						moves[0][1] = 0;
						moves[0][2] = cell.getCellX();
						moves[0][3] = 3;
						
						_cells[cell.getCellX()][3].setObject(_cells[cell.getCellX()][0].getObject());
						_cells[cell.getCellX()][0].setObject(null);
						
						_cells[cell.getCellX()][3].getObject().setMoved(true);
					}
				}
			}
			else if (selectedObject instanceof Pawn)
			{
				if (selectedObject.getOwner().equals("white"))
				{
					// Promotion.
					if (cell.getCellX() == 0)
					{
						if (GameConfig.CHOOSE_ON_PROMOTE)
						{
							ChessScreen.getInstance().getPromotionPanel().showSelectionWindow(_myColor, _selectedCell.getCellX(), _selectedCell.getCellY(), cell.getCellX(), cell.getCellY());
							return;
						}
						
						final Queen queen = new Queen(0, 0, selectedObject.getOwner());
						queen.setMoved(true);
						
						_allies.remove(cell.getObject());
						_allies.add(queen);
						
						cell.setObject(queen);
					}
					// EnPassing creation, if a pawn tries to avoid a kill by enemy pawn going 2 steps forward.
					// Then enemy pawn can go behind it to make a kill.
					else if (cell.getCellX() == _selectedCell.getCellX() - 2)
					{
						boolean makeInPassing = false;
						if (cell.getCellY() - 1 >= 0)
						{
							final ChessCell enemyCell = _cells[cell.getCellX()][cell.getCellY() - 1];
							if (enemyCell.getObject() instanceof Pawn && !enemyCell.getObject().isAlly(selectedObject.getOwner()))
								makeInPassing = true;
						}
						if (!makeInPassing && cell.getCellY() + 1 < 8)
						{
							final ChessCell enemyCell = _cells[cell.getCellX()][cell.getCellY() + 1];
							if (enemyCell.getObject() instanceof Pawn && !enemyCell.getObject().isAlly(selectedObject.getOwner()))
								makeInPassing = true;
						}
						if (makeInPassing)
						{
							images[0] = "inPassing_make";
							
							moves[0][0] = -1;
							moves[0][1] = -1;
							moves[0][2] = cell.getCellX() + 1;
							moves[0][3] = cell.getCellY();
						}
					}
					// EnPassing kill.
					else if (_inPassing[0] != -1 && _inPassing[1] != -1 && _inPassing[0] == cell.getCellX() && _inPassing[1] == cell.getCellY())
					{
						_enemies.remove(_cells[cell.getCellX() + 1][cell.getCellY()].getObject());
						
						_cells[cell.getCellX() + 1][cell.getCellY()].setObject(null);
						
						images[0] = "inPassing_kill";
						
						moves[0][0] = _selectedCell.getCellX();
						moves[0][1] = _selectedCell.getCellY();
						moves[0][2] = cell.getCellX() + 1;
						moves[0][3] = cell.getCellY();
					}
				}
				else
				{
					// Promotion.
					if (cell.getCellX() == 7)
					{
						if (GameConfig.CHOOSE_ON_PROMOTE)
						{
							ChessScreen.getInstance().getPromotionPanel().showSelectionWindow(_myColor, _selectedCell.getCellX(), _selectedCell.getCellY(), cell.getCellX(), cell.getCellY());
							return;
						}
						
						final Queen queen = new Queen(0, 0, selectedObject.getOwner());
						queen.setMoved(true);
						
						_allies.remove(cell.getObject());
						_allies.add(queen);
						
						cell.setObject(queen);
					}
					// EnPassing creation, if a pawn tries to avoid a kill by enemy pawn going 2 steps forward.
					// Then enemy pawn can go behind it to make a kill.
					else if (cell.getCellX() == _selectedCell.getCellX() + 2)
					{
						boolean makeInPassing = false;
						if (cell.getCellY() - 1 >= 0)
						{
							final ChessCell enemyCell = _cells[cell.getCellX()][cell.getCellY() - 1];
							if (enemyCell.getObject() instanceof Pawn && !enemyCell.getObject().isAlly(selectedObject.getOwner()))
								makeInPassing = true;
						}
						if (!makeInPassing && cell.getCellY() + 1 < 8)
						{
							final ChessCell enemyCell = _cells[cell.getCellX()][cell.getCellY() + 1];
							if (enemyCell.getObject() instanceof Pawn && !enemyCell.getObject().isAlly(selectedObject.getOwner()))
								makeInPassing = true;
						}
						if (makeInPassing)
						{
							images[0] = "inPassing_make";
							
							moves[0][0] = -1;
							moves[0][1] = -1;
							moves[0][2] = cell.getCellX() - 1;
							moves[0][3] = cell.getCellY();
						}
					}
					// EnPassing kill.
					else if (_inPassing[0] != -1 && _inPassing[1] != -1 && _inPassing[0] == cell.getCellX() && _inPassing[1] == cell.getCellY())
					{
						_enemies.remove(_cells[cell.getCellX() - 1][cell.getCellY()].getObject());
						
						_cells[cell.getCellX() - 1][cell.getCellY()].setObject(null);
						
						images[0] = "inPassing_kill";
						
						moves[0][0] = _selectedCell.getCellX();
						moves[0][1] = _selectedCell.getCellY();
						moves[0][2] = cell.getCellX() - 1;
						moves[0][3] = cell.getCellY();
					}
				}
			}
			
			if (images[0] == null)
			{
				images[0] = selectedObject.getClass().getSimpleName();
				moves[0][0] = _selectedCell.getCellX();
				moves[0][1] = _selectedCell.getCellY();
				moves[0][2] = cell.getCellX();
				moves[0][3] = cell.getCellY();
				moves[1] = new int[] {-1, -1, -1, -1};
			}
			else
			{
				images[1] = selectedObject.getClass().getSimpleName();
				moves[1][0] = _selectedCell.getCellX();
				moves[1][1] = _selectedCell.getCellY();
				moves[1][2] = cell.getCellX();
				moves[1][3] = cell.getCellY();
			}
			
			selectedObject.setMoved(true);
			
			_selectedCell = null;
			_inPassing[0] = _inPassing[1] = -1;
			_myTurn = false;
			
			Client.getInstance().sendPacket(new RequestTurnChange(images, moves));
		}
	}
	
	public static ChessBoard getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ChessBoard INSTANCE = new ChessBoard();
	}
}