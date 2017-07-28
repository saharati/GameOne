package chess.objects;

import java.awt.Image;

import javax.swing.ImageIcon;

import chess.ChessCell;

public final class Bishop extends AbstractObject
{
	private static final Image WHITE_BISHOP = new ImageIcon(IMAGE_PATH + "bishop-white.png").getImage();
	private static final Image BLACK_BISHOP = new ImageIcon(IMAGE_PATH + "bishop-black.png").getImage();
	
	public Bishop(final int initialX, final int initialY, final String owner)
	{
		super(initialX, initialY, owner);
	}
	
	@Override
	public void buildPaths(final boolean isEnemy)
	{
		_path.clear();
		_extendedPath.clear();
		
		final ChessCell myCell = BOARD.getCell(this);
		
		// Main Diagonal
		boolean buildExtended = false;
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
			return WHITE_BISHOP;
		
		return BLACK_BISHOP;
	}
	
	@Override
	public int getScore()
	{
		return 3;
	}
}