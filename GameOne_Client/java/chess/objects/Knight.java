package chess.objects;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import chess.ChessCell;

/**
 * Chess Knight.
 * @author Sahar
 */
public final class Knight extends AbstractObject
{
	private static final Image WHITE_KNIGHT = new ImageIcon(IMAGE_PATH + "knight-white.png").getImage();
	private static final Image BLACK_KNIGHT = new ImageIcon(IMAGE_PATH + "knight-black.png").getImage();
	
	public Knight(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public List<ChessCell> getPathTo(final AbstractObject target)
	{
		// The only soldier with no path.
		return Collections.emptyList();
	}
	
	@Override
	public List<ChessCell> getRoute()
	{
		final ChessCell myCell = BOARD.getCell(this);
		final List<ChessCell> path = new ArrayList<>();
		
		int i = myCell.getCellX();
		int j = myCell.getCellY();
		
		i += 2;
		if (i < 8)
		{
			if (j + 1 < 8)
			{
				final ChessCell targetCell = BOARD.getCell(i, j + 1);
				if (targetCell.getObject() == null || !targetCell.getObject().isAlly(this))
					path.add(targetCell);
			}
			if (j - 1 >= 0)
			{
				final ChessCell targetCell = BOARD.getCell(i, j - 1);
				if (targetCell.getObject() == null || !targetCell.getObject().isAlly(this))
					path.add(targetCell);
			}
		}
		i -= 4;
		if (i >= 0)
		{
			if (j + 1 < 8)
			{
				final ChessCell targetCell = BOARD.getCell(i, j + 1);
				if (targetCell.getObject() == null || !targetCell.getObject().isAlly(this))
					path.add(targetCell);
			}
			if (j - 1 >= 0)
			{
				final ChessCell targetCell = BOARD.getCell(i, j - 1);
				if (targetCell.getObject() == null || !targetCell.getObject().isAlly(this))
					path.add(targetCell);
			}
		}
		i += 2;
		j += 2;
		if (j < 8)
		{
			if (i + 1 < 8)
			{
				final ChessCell targetCell = BOARD.getCell(i + 1, j);
				if (targetCell.getObject() == null || !targetCell.getObject().isAlly(this))
					path.add(targetCell);
			}
			if (i - 1 >= 0)
			{
				final ChessCell targetCell = BOARD.getCell(i - 1, j);
				if (targetCell.getObject() == null || !targetCell.getObject().isAlly(this))
					path.add(targetCell);
			}
		}
		j -= 4;
		if (j >= 0)
		{
			if (i + 1 < 8)
			{
				final ChessCell targetCell = BOARD.getCell(i + 1, j);
				if (targetCell.getObject() == null || !targetCell.getObject().isAlly(this))
					path.add(targetCell);
			}
			if (i - 1 >= 0)
			{
				final ChessCell targetCell = BOARD.getCell(i - 1, j);
				if (targetCell.getObject() == null || !targetCell.getObject().isAlly(this))
					path.add(targetCell);
			}
		}
		
		return path;
	}
	
	@Override
	public Image getImage()
	{
		if (getOwner().equals("white"))
			return WHITE_KNIGHT;
		
		return BLACK_KNIGHT;
	}
	
	@Override
	public int getScore()
	{
		return 2;
	}
}