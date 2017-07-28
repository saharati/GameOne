package checkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

public final class CheckersButton extends JButton
{
	private static final long serialVersionUID = 608473057363743650L;
	
	private final Color _bgColor;
	private String _color;
	
	public CheckersButton(final String image, final boolean turn)
	{
		if (image != null)
		{
			final String[] img = image.split("-");
			
			setName(img[0]);
			_color = img[1];
		}
		
		_bgColor = turn ? Color.WHITE : Color.BLACK;
		
		setBackground(_bgColor);
		setOpaque(true);
	}
	
	public String getColor()
	{
		return _color;
	}
	
	public void setImage(final String image, final boolean update)
	{
		if (image == null)
		{
			setName(null);
			_color = null;
		}
		else
		{
			final String[] img = image.split("-");
			
			setName(img[0]);
			_color = img[1];
		}
		
		if (update)
			repaint();
	}
	
	public Color getBackgroundColor()
	{
		return _bgColor;
	}
	
	public String getFullName()
	{
		return getName() + "-" + _color;
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		if (getName() != null)
		{
			final Dimension dim = getParent().getSize();
			g.drawImage(CheckersScreen.IMAGES.get(getFullName()), 0, 0, dim.width / CheckersScreen.BOARD_SIZE, dim.height / CheckersScreen.BOARD_SIZE, null);
		}
	}
}