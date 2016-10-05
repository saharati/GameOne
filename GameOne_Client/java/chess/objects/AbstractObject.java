package chess.objects;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
	
	// Path up to nearest available player, including that player.
	protected final List<ChessCell> _path = new CopyOnWriteArrayList<>();
	// The same as _path, just with 1 more object visible beyond the first one (if any).
	protected final List<ChessCell> _extendedPath = new CopyOnWriteArrayList<>();
	
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
	
	public final List<ChessCell> getPathToShow()
	{
		final List<ChessCell> path = new ArrayList<>();
		for (final ChessCell cell : _path)
			if (cell.getObject() == null || !cell.getObject().isAlly(getOwner()))
				path.add(cell);
		
		return path;
	}
	
	public final List<ChessCell> getPathIncludingAllies()
	{
		return _path;
	}
	
	public final List<ChessCell> getExtendedPath()
	{
		return _extendedPath;
	}
	
	public final List<AbstractObject> getEatableTargets()
	{
		final List<AbstractObject> objects = new ArrayList<>();
		for (final ChessCell cell : getPathToShow())
			if (cell.getObject() != null)
				objects.add(cell.getObject());
		
		return objects;
	}
	
	public final boolean canEat(final AbstractObject target)
	{
		return getEatableTargets().contains(target);
	}
	
	public final boolean canBlock(final List<ChessCell> pathToKing)
	{
		for (final ChessCell cell : getPathToShow())
			if (pathToKing.contains(cell))
				return true;
		
		return false;
	}
	
	public final boolean canSee(final ChessCell target)
	{
		if (target.getObject() == null)
			return _path.contains(target);
		if (isAlly(target.getObject().getOwner()))
			return _path.contains(target);
		
		return getPathToShow().contains(target);
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
		final King king = BOARD.getMyKing();
		final List<ChessCell> threats = BOARD.getThreateningCells(king);
		// If someone has king on target.
		if (!threats.isEmpty())
		{
			// Only 1 can threat king at a certain point.
			final AbstractObject threat = threats.get(0).getObject();
			// Get its path to the king.
			final List<ChessCell> pathToKing = threat.getPathTo(king);
			
			// If can't block AND can't eat, this soldier is paralyzed.
			if (!canBlock(pathToKing) && !canEat(threat))
				_path.removeAll(getPathToShow());
			// Allow only the paths that can block the way to king.
			else
			{
				for (final ChessCell cell : getPathToShow())
					if (!pathToKing.contains(cell) && cell != threats.get(0))
						_path.remove(cell);
			}
		}
		// Nobody has king on target.
		else
		{
			final List<ChessCell> attackers = BOARD.getThreateningCells(this);
			// If this soldier has attackers.
			if (!attackers.isEmpty())
			{
				final ChessCell myCell = BOARD.getCell(this);
				final ChessCell kingCell = BOARD.getCell(king);
				// Iterate attackers.
				for (final ChessCell attacker : attackers)
				{
					final List<ChessCell> extendedPath = attacker.getObject().getExtendedPath();
					final List<ChessCell> pathTo = attacker.getObject().getPathTo(king);
					// If attacker has king on his extended path AND this soldier is on path.
					if (extendedPath.contains(kingCell) && pathTo.contains(myCell))
					{
						// Do not allow this soldier to leave the path.
						for (final ChessCell cell : getPathToShow())
							if (!pathTo.contains(cell))
								_path.remove(cell);
					}
				}
			}
		}
	}
	
	public abstract void buildPaths();
	
	public abstract Image getImage();
	
	public abstract int getScore();
}