package chess.objects;

import java.awt.Image;

import javax.swing.ImageIcon;

import chess.ChessCell;

/**
 * Chess Bishop.
 * @author Sahar
 */
public final class Bishop extends AbstractObject
{
	private static final Image WHITE_BISHOP = new ImageIcon(IMAGE_PATH + "bishop-white.png").getImage();
	private static final Image BLACK_BISHOP = new ImageIcon(IMAGE_PATH + "bishop-black.png").getImage();
	
	public Bishop(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public void buildPath()
	{
		_path.clear();
		
		final ChessCell myCell = BOARD.getCell(this);
		// Main Diagonal
		for (int x = myCell.getCellX() + 1, y = myCell.getCellY() + 1;x < 8 && y < 8;x++, y++)
		{
			final ChessCell targetCell = BOARD.getCell(x, y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				_path.add(targetCell);
				break;
			}
			
			_path.add(targetCell);
		}
		for (int x = myCell.getCellX() - 1, y = myCell.getCellY() - 1;x >= 0 && y >= 0;x--, y--)
		{
			final ChessCell targetCell = BOARD.getCell(x, y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				_path.add(targetCell);
				break;
			}
			
			_path.add(targetCell);
		}
		// Sub Diagonal
		for (int x = myCell.getCellX() + 1, y = myCell.getCellY() - 1;x < 8 && y >= 0;x++, y--)
		{
			final ChessCell targetCell = BOARD.getCell(x, y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				_path.add(targetCell);
				break;
			}
			
			_path.add(targetCell);
		}
		for (int x = myCell.getCellX() - 1, y = myCell.getCellY() + 1;x >= 0 && y < 8;x--, y++)
		{
			final ChessCell targetCell = BOARD.getCell(x, y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				_path.add(targetCell);
				break;
			}
			
			_path.add(targetCell);
		}
	}
	
	@Override
	public Image getImage()
	{
		if (getOwner().equals("white"))
			return WHITE_BISHOP;
		
		return BLACK_BISHOP;
	}
	
	@Override
	public int getScore()
	{
		return 3;
	}
}