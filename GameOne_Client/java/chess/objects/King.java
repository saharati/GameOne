package chess.objects;

import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;

import chess.CheckStatus;
import chess.ChessCell;

public final class King extends AbstractObject
{
	private static final Image WHITE_KING = new ImageIcon(IMAGE_PATH + "king-white.png").getImage();
	private static final Image BLACK_KING = new ImageIcon(IMAGE_PATH + "king-black.png").getImage();
	
	public King(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public void buildPaths(final boolean isEnemy)
	{
		// No extended path since it can only go 1 spot toward each direction.
		_path.clear();
		
		final ChessCell myCell = BOARD.getCell(this);
		for (int x = myCell.getCellX() - 1;x <= myCell.getCellX() + 1;x++)
		{
			for (int y = myCell.getCellY() - 1;y <= myCell.getCellY() + 1;y++)
			{
				// Ignore self.
				if (x == myCell.getCellX() && y == myCell.getCellY())
					continue;
				// Ignore boundaries.
				if (x < 0 || y < 0 || x >= 8 || y >= 8)
					continue;
				
				_path.add(BOARD.getCell(x, y));
			}
		}
	}
	
	@Override
	public void validatePath()
	{
		for (final ChessCell cell : _path)
			if (BOARD.canBeSeenBy(getEnemy(), cell, false))
				_path.remove(cell);
		
		final ChessCell myCell = BOARD.getCell(this);
		final CheckStatus check = getCheckStatus();
		if (check == CheckStatus.NOT_UNDER_CHECK)
		{
			// Castling move.
			if (!hasMoved())
			{
				final ChessCell leftRookCell = BOARD.getCell(myCell.getCellX(), 0);
				final ChessCell rightRookCell = BOARD.getCell(myCell.getCellX(), 7);
				
				// Check that left object is still a Rook, hasn't moved, cannot be eaten and can see all the way to the king.
				if (leftRookCell.getObject() instanceof Rook && !leftRookCell.getObject().hasMoved() && leftRookCell.getObject().canSee(myCell, false))
				{
					// All way must be clear from enemies.
					boolean clear = true;
					for (int y = 0;y < myCell.getCellY();y++)
					{
						if (BOARD.canBeSeenBy(getEnemy(), BOARD.getCell(myCell.getCellX(), y), false))
						{
							clear = false;
							break;
						}
					}
					if (clear)
						_path.add(BOARD.getCell(myCell.getCellX(), 2));
				}
				// Check that right object is still a Rook, hasn't moved, cannot be eaten and can see all the way to the king.
				if (rightRookCell.getObject() instanceof Rook && !rightRookCell.getObject().hasMoved() && rightRookCell.getObject().canSee(myCell, false))
				{
					// All way must be clear from enemies.
					boolean clear = true;
					for (int y = rightRookCell.getCellY();y > myCell.getCellY();y--)
					{
						if (BOARD.canBeSeenBy(getEnemy(), BOARD.getCell(myCell.getCellX(), y), false))
						{
							clear = false;
							break;
						}
					}
					if (clear)
						_path.add(BOARD.getCell(myCell.getCellX(), 6));
				}
			}
		}
		else
		{
			final List<ChessCell> attackers = BOARD.getThreateningCells(this);
			if (!attackers.isEmpty())
			{
				final ChessCell attacker = attackers.get(0);
				for (final ChessCell cell : _path)
					if (attacker.getObject().canSee(cell, true))
						_path.remove(cell);
			}
		}
	}
	
	@Override
	public Image getImage()
	{
		if (getOwner().equals("white"))
			return WHITE_KING;
		
		return BLACK_KING;
	}
	
	@Override
	public int getScore()
	{
		return 0;
	}
	
	public CheckStatus getCheckStatus()
	{
		final List<ChessCell> attackers = BOARD.getThreateningCells(this);
		if (!attackers.isEmpty())
		{
			// If king can move somewhere, its a check.
			if (!getPathToShow().isEmpty())
				return CheckStatus.UNDER_CHECK;
			// If target can be eaten, its a check.
			if (BOARD.canBeEaten(attackers.get(0).getObject()))
				return CheckStatus.UNDER_CHECK;
			// If target can be blocked, its a check.
			final List<ChessCell> pathToKing = attackers.get(0).getObject().getPathTo(this);
			if (BOARD.canBeBlocked(this, pathToKing))
				return CheckStatus.UNDER_CHECK;
			
			return CheckStatus.UNDER_CHECKMATE;
		}
		
		return CheckStatus.NOT_UNDER_CHECK;
	}
}