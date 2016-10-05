package chess.objects;

import java.awt.Image;
import java.util.List;

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
	
	private ChessCell _attackLeft;
	private ChessCell _attackRight;
	
	public Pawn(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public void buildPath()
	{
		_path.clear();
		_attackLeft = null;
		_attackRight = null;
		
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
			_path.add(targetCell);
			
			if (targetCell.getObject() == null || targetCell.getObject().isAlly(getOwner()))
				_attackRight = targetCell;
		}
		// Check left for enemies.
		if (myCell.getCellY() - 1 >= 0)
		{
			final ChessCell targetCell = BOARD.getCell(i, myCell.getCellY() - 1);
			_path.add(targetCell);
			
			if (targetCell.getObject() == null || targetCell.getObject().isAlly(getOwner()))
				_attackLeft = targetCell;
		}
		
		// Check for en-passing move.
		final int[] enPassing = BOARD.getInPassing();
		if (enPassing[0] == i && (enPassing[1] == myCell.getCellY() - 1 || enPassing[1] == myCell.getCellY() + 1))
			_path.add(BOARD.getCell(enPassing[0], enPassing[1]));
	}
	
	@Override
	public List<ChessCell> getPossiblePath()
	{
		final List<ChessCell> path = super.getPossiblePath();
		if (_attackLeft != null)
			path.remove(_attackLeft);
		if (_attackRight != null)
			path.remove(_attackRight);
		
		return path;
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