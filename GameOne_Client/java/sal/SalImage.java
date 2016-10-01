package sal;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * A certain image in slide a lama board.
 * @author Sahar
 */
public final class SalImage extends JPanel
{
	private static final long serialVersionUID = -5956612606155953640L;
	
	private Image _image;
	
	public SalImage()
	{
		setPreferredSize(SalScreen.SQAURE_SIZE);
	}
	
	public SalImage(final String name)
	{
		setPreferredSize(SalScreen.SQAURE_SIZE);
		setImage(name);
	}
	
	public void setImage(final String name)
	{
		setName(name);
		
		_image = SalScreen.IMAGES.get(name);
		
		repaint();
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		if (_image != null)
			g.drawImage(_image, 0, 0, null);
	}
}