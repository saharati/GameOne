package chess.objects;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import chess.ChessCell;

/**
 * Chess Rook.
 * @author Sahar
 */
public final class Rook extends AbstractObject
{
	private static final Image WHITE_ROOK = new ImageIcon(IMAGE_PATH + "rook-white.png").getImage();
	private static final Image BLACK_ROOK = new ImageIcon(IMAGE_PATH + "rook-black.png").getImage();
	
	public Rook(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public List<ChessCell> getRoute()
	{
		final ChessCell myCell = BOARD.getCell(this);
		final List<ChessCell> path = new ArrayList<>();
		// Horizontal
		for (int x = myCell.getCellX() + 1;x < 8;x++)
		{
			final ChessCell targetCell = BOARD.getCell(x, myCell.getCellY());
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (!targetObj.isAlly(this))
					path.add(targetCell);
				
				break;
			}
			
			path.add(targetCell);
		}
		for (int x = myCell.getCellX() - 1;x >= 0;x--)
		{
			final ChessCell targetCell = BOARD.getCell(x, myCell.getCellY());
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (!targetObj.isAlly(this))
					path.add(targetCell);
				
				break;
			}
			
			path.add(targetCell);
		}
		// Vertical
		for (int y = myCell.getCellY() + 1;y < 8;y++)
		{
			final ChessCell targetCell = BOARD.getCell(myCell.getCellX(), y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (!targetObj.isAlly(this))
					path.add(targetCell);
				
				break;
			}
			
			path.add(targetCell);
		}
		for (int y = myCell.getCellY() - 1;y >= 0;y--)
		{
			final ChessCell targetCell = BOARD.getCell(myCell.getCellX(), y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (!targetObj.isAlly(this))
					path.add(targetCell);
				
				break;
			}
			
			path.add(targetCell);
		}
		
		return path;
	}
	
	@Override
	public Image getImage()
	{
		if (getOwner().equals("white"))
			return WHITE_ROOK;
		
		return BLACK_ROOK;
	}
	
	@Override
	public int getScore()
	{
		return 4;
	}
}