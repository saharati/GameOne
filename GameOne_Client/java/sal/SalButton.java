package sal;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JButton;

/**
 * A button used to push an image in slide a lama board.
 * @author Sahar
 */
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