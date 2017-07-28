package chess.objects;

import java.awt.Image;

import javax.swing.ImageIcon;

import chess.ChessCell;

public final class Rook extends AbstractObject
{
	private static final Image WHITE_ROOK = new ImageIcon(IMAGE_PATH + "rook-white.png").getImage();
	private static final Image BLACK_ROOK = new ImageIcon(IMAGE_PATH + "rook-black.png").getImage();
	
	public Rook(final int initialX, final int initialY, final String owner)
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
		
		_extendedPath.addAll(_path);
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