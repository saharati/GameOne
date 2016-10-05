package chess.objects;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

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
	
	public final boolean isAlly(final AbstractObject other)
	{
		return _owner.equals(other.getOwner());
	}
	
	public final boolean hasMoved()
	{
		return _hasMoved;
	}
	
	public final void setMoved(final boolean moved)
	{
		_hasMoved = moved;
	}
	
	public final List<AbstractObject> getEatableTargets()
	{
		final List<AbstractObject> objects = new ArrayList<>();
		for (final ChessCell cell : getRoute())
		{
			final AbstractObject obj = cell.getObject();
			if (obj != null && !obj.isAlly(this))
				objects.add(obj);
		}
		
		return objects;
	}
	
	public final boolean canEat(final AbstractObject object)
	{
		return getEatableTargets().contains(object);
	}
	
	public final boolean canSeeTarget(final AbstractObject target)
	{
		for (final ChessCell cell : getPathTo(target))
			if (cell.getObject() != null)
				return false;
		
		return true;
	}
	
	public final boolean canBlock(final List<ChessCell> targetPath)
	{
		for (final ChessCell cell : getRoute())
			if (targetPath.contains(cell))
				return true;
		
		return false;
	}
	
	public List<ChessCell> getPathTo(final AbstractObject target)
	{
		final List<ChessCell> path = new ArrayList<>();
		final ChessCell from = BOARD.getCell(this);
		final ChessCell to = BOARD.getCell(target);
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
	
	public abstract List<ChessCell> getRoute();
	
	public abstract Image getImage();
	
	public abstract int getScore();
}