package chess;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Class used to make imba dialogs for chess.
 * @author Sahar
 */
public final class ChessBackground extends JOptionPane
{
	private static final long serialVersionUID = -8749668351445304451L;
	
	private static final Dimension BACKGROUND_SIZE = new Dimension(600, 450);
	private static final String BACKGROUND_PATH = "./images/chess/backgrounds/";
	
	public static final Image WON = new ImageIcon(BACKGROUND_PATH + "won.jpg").getImage();
	public static final Image LOST = new ImageIcon(BACKGROUND_PATH + "lost.jpg").getImage();
	public static final Image TIE = new ImageIcon(BACKGROUND_PATH + "tie.jpg").getImage();
	public static final Image OFF = new ImageIcon(BACKGROUND_PATH + "off.jpg").getImage();
	public static final Image CHOOSE = new ImageIcon(BACKGROUND_PATH + "choose.jpg").getImage();
	
	private Image _image;
	
	public ChessBackground()
	{
		super("");
	}
	
	public void showDialog(final String title, final Image image)
	{
		_image = image;
		
		final JDialog dialog = createDialog(title);
		dialog.getContentPane().setPreferredSize(BACKGROUND_SIZE);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.dispose();
	}
	
	@Override
    public void paint(final Graphics g)
    {
		super.paint(g);
		
		if (_image != null)
			g.drawImage(_image, 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height, null);
    }
	
	public static ChessBackground getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ChessBackground INSTANCE = new ChessBackground();
	}
}