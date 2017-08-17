package mario.resources;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import mario.SuperMario;
import objects.mario.MarioType;

public final class BackgroundPanel extends JPanel
{
	private static final long serialVersionUID = -4624064378463091449L;
	
	private ImageIcon _currentBackground = MarioType.BACKGROUND.getIcon();
	
	public BackgroundPanel(final JPanel mapHolder)
	{
		super(null);
		
		setPreferredSize(SuperMario.SCREEN_SIZE);
		add(mapHolder);
	}
	
	public void changeBackground(final ImageIcon background)
	{
		if (background == _currentBackground)
			return;
		
		_currentBackground = background;
		
		repaint();
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		g.drawImage(_currentBackground.getImage(), 0, 0, null);
	}
}