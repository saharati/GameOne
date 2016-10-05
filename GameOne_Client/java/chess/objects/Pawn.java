package chess.objects;

import java.awt.Image;

import javax.swing.ImageIcon;

import chess.ChessCell;

/**
 * Chess Pawn.
 * @author Sahar
 */
public final class Pawn extends AbstractObject
{
	private static final Image WHITE_PAWN = new ImageIcon(IMAGE_PATH + "pawn-white.png").getImage();
	private static final Image BLACK_PAWN = new ImageIcon(IMAGE_PATH + "pawn-black.png").getImage();
	
	public Pawn(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public void buildPaths()
	{
		// No extended path since pawn can only go 1 step when it eats.
		_path.clear();
		
		final ChessCell myCell = BOARD.getCell(this);
		final int i = getOwner().equals("white") ? myCell.getCellX() - 1 : myCell.getCellX() + 1;
		if (getOwner().equals("white"))
		{
			// If hasn't moved yet.
			if (!hasMoved())
			{
				// Can make 2 steps, if possible.
				for (int x = i;x > i - 2;x--)
				{
					final ChessCell targetCell = BOARD.getCell(x, myCell.getCellY());
					if (targetCell.getObject() != null)
						break;
					
					_path.add(targetCell);
				}
			}
			// Have moved.
			else
			{
				// 1 step, if possible.
				final ChessCell targetCell = BOARD.getCell(i, myCell.getCellY());
				if (targetCell.getObject() == null)
					_path.add(targetCell);
			}
		}
		else
		{
			// If hasn't moved yet.
			if (!hasMoved())
			{
				// Can make 2 steps, if possible.
				for (int x = i;x < i + 2;x++)
				{
					final ChessCell targetCell = BOARD.getCell(x, myCell.getCellY());
					if (targetCell.getObject() != null)
						break;
					
					_path.add(targetCell);
				}
			}
			// Have moved.
			else
			{
				// 1 step, if possible.
				final ChessCell targetCell = BOARD.getCell(i, myCell.getCellY());
				if (targetCell.getObject() == null)
					_path.add(targetCell);
			}
		}
		
		// Check right for enemies.
		if (myCell.getCellY() + 1 < 8)
		{
			final ChessCell targetCell = BOARD.getCell(i, myCell.getCellY() + 1);
			if (targetCell.getObject() != null && !targetCell.getObject().isAlly(getOwner()))
				_path.add(targetCell);
		}
		// Check left for enemies.
		if (myCell.getCellY() - 1 >= 0)
		{
			final ChessCell targetCell = BOARD.getCell(i, myCell.getCellY() - 1);
			if (targetCell.getObject() != null && !targetCell.getObject().isAlly(getOwner()))
				_path.add(targetCell);
		}
		
		// Check for en-passing move.
		final int[] enPassing = BOARD.getInPassing();
		if (enPassing[0] == i && (enPassing[1] == myCell.getCellY() - 1 || enPassing[1] == myCell.getCellY() + 1))
			_path.add(BOARD.getCell(enPassing[0], enPassing[1]));
	}
	
	@Override
	public Image getImage()
	{
		if (getOwner().equals("white"))
			return WHITE_PAWN;
		
		return BLACK_PAWN;
	}
	
	@Override
	public int getScore()
	{
		return 1;
	}
}