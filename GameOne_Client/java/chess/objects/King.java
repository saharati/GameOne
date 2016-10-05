package chess.objects;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import chess.CheckStatus;
import chess.ChessCell;

/**
 * Chess king.
 * @author Sahar
 */
public final class King extends AbstractObject
{
	private static final Image WHITE_KING = new ImageIcon(IMAGE_PATH + "king-white.png").getImage();
	private static final Image BLACK_KING = new ImageIcon(IMAGE_PATH + "king-black.png").getImage();
	
	public King(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public List<ChessCell> getRoute()
	{
		final ChessCell myCell = BOARD.getCell(this);
		final List<ChessCell> path = new ArrayList<>();
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
				
				final ChessCell targetCell = BOARD.getCell(x, y);
				final AbstractObject targetObj = targetCell.getObject();
				// If cell isn't owned by anyone or is owned by enemy.
				if (targetObj == null || !targetObj.isAlly(this))
				{
					// Move king to target cell temporary.
					myCell.setObject(null, false);
					targetCell.setObject(this, false);
					
					// Check if it cannot be eaten after moving.
					if (!BOARD.canBeEaten(this))
						path.add(targetCell);
					
					// Add back previous object.
					myCell.setObject(this, false);
					targetCell.setObject(targetObj, false);
				}
			}
		}
		
		// Castling move.
		if (!hasMoved() && BOARD.getCheckStatus(this, path.size() > 0) == CheckStatus.NOT_UNDER_CHECK)
		{
			final ChessCell leftRookCell = BOARD.getCell(myCell.getCellX(), 0);
			final ChessCell rightRookCell = BOARD.getCell(myCell.getCellX(), 7);
			
			// Check that left object is still a Rook, hasn't moved, cannot be eaten and can see all the way to the king.
			if (leftRookCell.getObject() instanceof Rook && !leftRookCell.getObject().hasMoved() && leftRookCell.getObject().canSeeTarget(this) && !BOARD.canBeEaten(leftRookCell.getObject()))
			{
				// All way must be clear from enemies.
				boolean clear = true;
				for (int y = 0;y < myCell.getCellY();y++)
				{
					if (BOARD.canBeSeenBy(getEnemy(), BOARD.getCell(myCell.getCellX(), y)))
					{
						clear = false;
						break;
					}
				}
				if (clear)
					path.add(BOARD.getCell(myCell.getCellX(), 2));
			}
			// Check that right object is still a Rook, hasn't moved, cannot be eaten and can see all the way to the king.
			if (rightRookCell.getObject() instanceof Rook && !rightRookCell.getObject().hasMoved() && rightRookCell.getObject().canSeeTarget(this) && !BOARD.canBeEaten(rightRookCell.getObject()))
			{
				// All way must be clear from enemies.
				boolean clear = true;
				for (int y = rightRookCell.getCellY();y > myCell.getCellY();y--)
				{
					if (BOARD.canBeSeenBy(getEnemy(), BOARD.getCell(myCell.getCellX(), y)))
					{
						clear = false;
						break;
					}
				}
				if (clear)
					path.add(BOARD.getCell(myCell.getCellX(), 6));
			}
		}
		
		return path;
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
}