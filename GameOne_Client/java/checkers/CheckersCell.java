package checkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

public final class CheckersCell extends JButton
{
	private static final long serialVersionUID = 608473057363743650L;
	
	private final Color _originalBackground;
	private final int _i;
	private final int _j;
	private String _image;
	
	public CheckersCell(final String image, final int i, final int j, final boolean isWhite)
	{
		_image = image;
		_i = i;
		_j = j;
		_originalBackground = isWhite ? Color.WHITE : Color.BLACK;
		
		setBackground(_originalBackground);
		setOpaque(true);
	}
	
	public void setImage(final String image, final boolean update)
	{
		_image = image;
		
		if (update)
			repaint();
	}
	
	public String getImage()
	{
		return _image;
	}
	
	public int getI()
	{
		return _i;
	}
	
	public int getJ()
	{
		return _j;
	}
	
	public Color getOriginalBackground()
	{
		return _originalBackground;
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		if (!_image.isEmpty())
		{
			final Dimension dim = getParent().getSize();
			g.drawImage(CheckersScreen.IMAGES.get(_image), 0, 0, dim.width / CheckersScreen.BOARD_SIZE, dim.height / CheckersScreen.BOARD_SIZE, null);
		}
	}
}