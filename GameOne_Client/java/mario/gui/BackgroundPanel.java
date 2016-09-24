package mario.gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import mario.MarioBuilder;

/**
 * Background panel for Pacman, panel "moves" with screen.
 * @author Sahar
 */
public final class BackgroundPanel extends JPanel
{
	private static final long serialVersionUID = -4624064378463091449L;
	
	public static final Image BACKGROUND_NORMAL = new ImageIcon(MarioBuilder.IMAGE_PATH + "background.png").getImage();
	public static final Image BACKGROUND_BOSS = new ImageIcon(MarioBuilder.IMAGE_PATH + "background2.png").getImage();
	
	private Image _currentBackground = BACKGROUND_NORMAL;
	
	public BackgroundPanel(final JPanel mapHolder)
	{
		super(null);
		
		setPreferredSize(MarioBuilder.SCREEN_SIZE);
		
		add(mapHolder);
	}
	
	public void changeBackground(final Image background)
	{
		if (background == _currentBackground)
			return;
		
		_currentBackground = background;
		
		repaint();
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		g.drawImage(_currentBackground, 0, 0, _currentBackground.getWidth(this), _currentBackground.getHeight(this), this);
	}
}