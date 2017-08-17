package sal;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JButton;

public final class SalButton extends JButton
{
	private static final long serialVersionUID = -3925153250784725588L;
	
	private final Image _image;
	
	public SalButton(final Image image)
	{
		_image = image;
		
		setPreferredSize(SalScreen.SQAURE_SIZE);
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		g.drawImage(_image, 0, 0, null);
	}
}