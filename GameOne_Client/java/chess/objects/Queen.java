package chess.objects;

import java.awt.Image;

import javax.swing.ImageIcon;

import chess.ChessCell;

/**
 * Chess Queen.
 * @author Sahar
 */
public final class Queen extends AbstractObject
{
	private static final Image WHITE_QUEEN = new ImageIcon(IMAGE_PATH + "queen-white.png").getImage();
	private static final Image BLACK_QUEEN = new ImageIcon(IMAGE_PATH + "queen-black.png").getImage();
	
	public Queen(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public void buildPaths(final boolean isEnemy)
	{
		_path.clear();
		_extendedPath.clear();
		
		final ChessCell myCell = BOARD.getCell(this);
		
		// Horizontal
		boolean buildExtended = false;
		for (int x = myCell.getCellX() + 1;x < 8;x++)
		{
			final ChessCell targetCell = BOARD.getCell(x, myCell.getCellY());
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (buildExtended)
				{
					_extendedPath.add(targetCell);
					break;
				}
				
				_path.add(targetCell);
				buildExtended = true;
				continue;
			}
			
			if (buildExtended)
				_extendedPath.add(targetCell);
			else
				_path.add(targetCell);
		}
		buildExtended = false;
		for (int x = myCell.getCellX() - 1;x >= 0;x--)
		{
			final ChessCell targetCell = BOARD.getCell(x, myCell.getCellY());
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (buildExtended)
				{
					_extendedPath.add(targetCell);
					break;
				}
				
				_path.add(targetCell);
				buildExtended = true;
				continue;
			}
			
			if (buildExtended)
				_extendedPath.add(targetCell);
			else
				_path.add(targetCell);
		}
		// Vertical
		buildExtended = false;
		for (int y = myCell.getCellY() + 1;y < 8;y++)
		{
			final ChessCell targetCell = BOARD.getCell(myCell.getCellX(), y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (buildExtended)
				{
					_extendedPath.add(targetCell);
					break;
				}
				
				_path.add(targetCell);
				buildExtended = true;
				continue;
			}
			
			if (buildExtended)
				_extendedPath.add(targetCell);
			else
				_path.add(targetCell);
		}
		buildExtended = false;
		for (int y = myCell.getCellY() - 1;y >= 0;y--)
		{
			final ChessCell targetCell = BOARD.getCell(myCell.getCellX(), y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (buildExtended)
				{
					_extendedPath.add(targetCell);
					break;
				}
				
				_path.add(targetCell);
				buildExtended = true;
				continue;
			}
			
			if (buildExtended)
				_extendedPath.add(targetCell);
			else
				_path.add(targetCell);
		}
		// Main Diagonal
		buildExtended = false;
		for (int x = myCell.getCellX() + 1, y = myCell.getCellY() + 1;x < 8 && y < 8;x++, y++)
		{
			final ChessCell targetCell = BOARD.getCell(x, y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (buildExtended)
				{
					_extendedPath.add(targetCell);
					break;
				}
				
				_path.add(targetCell);
				buildExtended = true;
				continue;
			}
			
			if (buildExtended)
				_extendedPath.add(targetCell);
			else
				_path.add(targetCell);
		}
		buildExtended = false;
		for (int x = myCell.getCellX() - 1, y = myCell.getCellY() - 1;x >= 0 && y >= 0;x--, y--)
		{
			final ChessCell targetCell = BOARD.getCell(x, y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (buildExtended)
				{
					_extendedPath.add(targetCell);
					break;
				}
				
				_path.add(targetCell);
				buildExtended = true;
				continue;
			}
			
			if (buildExtended)
				_extendedPath.add(targetCell);
			else
				_path.add(targetCell);
		}
		// Sub Diagonal
		buildExtended = false;
		for (int x = myCell.getCellX() + 1, y = myCell.getCellY() - 1;x < 8 && y >= 0;x++, y--)
		{
			final ChessCell targetCell = BOARD.getCell(x, y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (buildExtended)
				{
					_extendedPath.add(targetCell);
					break;
				}
				
				_path.add(targetCell);
				buildExtended = true;
				continue;
			}
			
			if (buildExtended)
				_extendedPath.add(targetCell);
			else
				_path.add(targetCell);
		}
		buildExtended = false;
		for (int x = myCell.getCellX() - 1, y = myCell.getCellY() + 1;x >= 0 && y < 8;x--, y++)
		{
			final ChessCell targetCell = BOARD.getCell(x, y);
			final AbstractObject targetObj = targetCell.getObject();
			if (targetObj != null)
			{
				if (buildExtended)
				{
					_extendedPath.add(targetCell);
					break;
				}
				
				_path.add(targetCell);
				buildExtended = true;
				continue;
			}
			
			if (buildExtended)
				_extendedPath.add(targetCell);
			else
				_path.add(targetCell);
		}
		
		_extendedPath.addAll(_path);
	}
	
	@Override
	public Image getImage()
	{
		if (getOwner().equals("white"))
			return WHITE_QUEEN;
		
		return BLACK_QUEEN;
	}
	
	@Override
	public int getScore()
	{
		return 5;
	}
}