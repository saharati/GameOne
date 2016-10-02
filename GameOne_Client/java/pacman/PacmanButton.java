package pacman;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Map.Entry;

import javax.swing.JButton;

import objects.pacman.PacmanObject;

/**
 * Buttons used in pacman map builder to place objects.
 * @author Sahar
 */
public final class PacmanButton extends JButton
{
	private static final long serialVersionUID = -3053069584521532928L;
	
	private PacmanObject _key;
	private Image _value;
	
	public PacmanButton(final Entry<PacmanObject, Image> entry)
	{
		_key = entry.getKey();
		_value = entry.getValue();
		
		setPreferredSize(PacmanBuilder.PIXEL_DIMENSIONS);
	}
	
	public void setEntry(final PacmanObject key, final Image value)
	{
		_key = key;
		_value = value;
		
		repaint();
	}
	
	public void setEntry(final Entry<PacmanObject, Image> entry)
	{
		setEntry(entry.getKey(), entry.getValue());
	}
	
	public PacmanObject getKey()
	{
		return _key;
	}
	
	public Image getValue()
	{
		return _value;
	}
	
	@Override
	public Color getBackground()
	{
		return Color.BLACK;
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		g.drawImage(_value, 0, 0, null);
	}
}