package chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

/**
 * A single button in the chess board.
 * @author Sahar
 */
public final class ChessButton extends JButton
{
	private static final long serialVersionUID = 608473057363743650L;
	
	private static final Dimension BLOCK_SIZE = new Dimension(100, 100);
	
	private final Color _bgColor;
	private String _name;
	private String _color;
	private boolean _moved;
	
	public ChessButton(final String image, final boolean turn)
	{
		if (image != null)
		{
			final String[] img = image.split("-");
			
			_name = img[0];
			_color = img[1];
		}
		
		_bgColor = turn ? Color.GRAY : Color.LIGHT_GRAY;
		
		setPreferredSize(BLOCK_SIZE);
		setBackground(_bgColor);
		setOpaque(true);
	}
	
	public boolean hasMoved()
	{
		return _moved;
	}
	
	public void setMoved()
	{
		_moved = true;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getColor()
	{
		return _color;
	}
	
	public void setImage(final String image, final boolean update)
	{
		if (image == null)
		{
			_name = null;
			_color = null;
		}
		else
		{
			final String[] img = image.split("-");
			
			_name = img[0];
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
		return _name + "-" + _color;
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		if (_name != null)
			g.drawImage(ChessScreen.IMAGES.get(getFullName()), 0, 0, getWidth(), getHeight(), null);
	}
}