package chess.objects;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import chess.CheckStatus;
import chess.ChessBoard;
import chess.ChessCell;

/**
 * A soldier on chess board.
 * @author Sahar
 */
public abstract class AbstractObject
{
	protected static final ChessBoard BOARD = ChessBoard.getInstance();
	protected static final String IMAGE_PATH = "./images/chess/";
	
	protected final List<ChessCell> _path = new CopyOnWriteArrayList<>();
	
	private final int _initialX;
	private final int _initialY;
	private final String _owner;
	
	private boolean _hasMoved;
	
	protected AbstractObject(final int initialX, final int initialY, final String owner)
	{
		_initialX = initialX;
		_initialY = initialY;
		_owner = owner;
	}
	
	public final int getInitialX()
	{
		return _initialX;
	}
	
	public final int getInitialY()
	{
		return _initialY;
	}
	
	public final String getOwner()
	{
		return _owner;
	}
	
	public final String getEnemy()
	{
		return _owner.equals("white") ? "black" : "white";
	}
	
	public final boolean isAlly(final String other)
	{
		return _owner.equals(other);
	}
	
	public final boolean hasMoved()
	{
		return _hasMoved;
	}
	
	public final void setMoved(final boolean moved)
	{
		_hasMoved = moved;
	}
	
	public final List<ChessCell> getFullPath()
	{
		return _path;
	}
	
	public final List<AbstractObject> getEatableTargets()
	{
		final List<AbstractObject> objects = new ArrayList<>();
		for (final ChessCell cell : getPossiblePath())
			if (cell.getObject() != null)
				objects.add(cell.getObject());
		
		return objects;
	}
	
	public final boolean canEat(final AbstractObject target)
	{
		return getEatableTargets().contains(target);
	}
	
	public final boolean canSee(final ChessCell target)
	{
		if (target.getObject() == null)
			return _path.contains(target);
		if (isAlly(target.getObject().getOwner()))
			return _path.contains(target);
		
		return getPossiblePath().contains(target);
	}
	
	public final boolean canBlock(final List<ChessCell> pathToKing)
	{
		for (final ChessCell cell : getPossiblePath())
			if (pathToKing.contains(cell))
				return true;
		
		return false;
	}
	
	public List<ChessCell> getPossiblePath()
	{
		final List<ChessCell> path = new ArrayList<>();
		for (final ChessCell cell : _path)
			if (cell.getObject() == null || !cell.getObject().isAlly(getOwner()))
				path.add(cell);
		
		return path;
	}
	
	public List<ChessCell> getPathTo(final King king)
	{
		final List<ChessCell> path = new ArrayList<>();
		final ChessCell from = BOARD.getCell(this);
		final ChessCell to = BOARD.getCell(king);
		// Horizontal
		if (from.getCellX() == to.getCellX())
		{
			if (from.getCellY() < to.getCellY())
				for (int y = from.getCellY() + 1;y < to.getCellY();y++)
					path.add(BOARD.getCell(from.getCellX(), y));
			else
				for (int y = to.getCellY() - 1;y > from.getCellY();y--)
					path.add(BOARD.getCell(from.getCellX(), y));
		}
		// Vertical
		else if (from.getCellY() == to.getCellY())
		{
			if (from.getCellX() < to.getCellX())
				for (int x = from.getCellX() + 1;x < to.getCellX();x++)
					path.add(BOARD.getCell(x, from.getCellY()));
			else
				for (int x = to.getCellX() - 1;x > from.getCellX();x--)
					path.add(BOARD.getCell(x, from.getCellY()));
		}
		// Main Diagonal
		else if (from.getCellX() < to.getCellX() && from.getCellY() < to.getCellY())
			for (int x = from.getCellX() + 1, y = from.getCellY() + 1;x < to.getCellX() && y < to.getCellY();x++, y++)
				path.add(BOARD.getCell(x, y));
		else if (from.getCellX() > to.getCellX() && from.getCellY() > to.getCellY())
			for (int x = to.getCellX() - 1, y = to.getCellY() - 1;x > from.getCellX() && y > from.getCellY();x--, y--)
				path.add(BOARD.getCell(x, y));
		// Sub Diagonal
		else if (from.getCellX() < to.getCellX() && from.getCellY() > to.getCellY())
			for (int x = from.getCellX() + 1, y = to.getCellY() - 1;x < to.getCellX() && y > from.getCellY();x++, y--)
				path.add(BOARD.getCell(x, y));
		else if (from.getCellX() > to.getCellX() && from.getCellY() < to.getCellY())
			for (int x = to.getCellX() - 1, y = from.getCellY() + 1;x > from.getCellX() && y < to.getCellY();x--, y++)
				path.add(BOARD.getCell(x, y));
		
		return path;
	}
	
	public void validatePath()
	{
		final ChessCell myCell = BOARD.getCell(this);
		final King king = BOARD.getMyKing();
		final List<ChessCell> threats = BOARD.getThreateningCells(king);
		if (!threats.isEmpty())
		{
			final List<ChessCell> pathToKing = threats.get(0).getObject().getPathTo(king);
			
			// If can't block path, then can't go anywhere.
			if (!canBlock(pathToKing) && !canEat(threats.get(0).getObject()))
				_path.removeAll(getPossiblePath());
			// Allow only the paths that can block the way to king.
			else
			{
				for (final ChessCell cell : getPossiblePath())
					if (!pathToKing.contains(cell))
						_path.remove(cell);
			}
		}
		else
		{
			final List<ChessCell> attackers = BOARD.getThreateningCells(this);
			if (!attackers.isEmpty())
			{
				// Try moving the knight temporary to each cell in its path and check that it doesn't reveal the king to a check.
				myCell.setObject(null, false);
				for (final ChessCell cell : getPossiblePath())
				{
					final AbstractObject cellObj = cell.getObject();
					
					cell.setObject(this, false);
					
					// Rebuild paths for attackers after this object has temporary moved to see if it reavels king to attack.
					attackers.forEach(attacker -> attacker.getObject().buildPath());
					
					if (king.getCheckStatus() != CheckStatus.NOT_UNDER_CHECK)
						_path.remove(cell);
					
					cell.setObject(cellObj, false);
				}
				myCell.setObject(this, false);
				
				// Finally put back paths to how they were before moving.
				attackers.forEach(attacker -> attacker.getObject().buildPath());
			}
		}
	}
	
	public abstract void buildPath();
	
	public abstract Image getImage();
	
	public abstract int getScore();
}