package checkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

/**
 * A single button in the checkers board.
 * @author Sahar
 */
public final class CheckersButton extends JButton
{
	private static final long serialVersionUID = 608473057363743650L;
	
	private static final Dimension BLOCK_SIZE = new Dimension(50, 50);
	
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
		
		setPreferredSize(BLOCK_SIZE);
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
			g.drawImage(CheckersScreen.IMAGES.get(getFullName()), 0, 0, null);
	}
}