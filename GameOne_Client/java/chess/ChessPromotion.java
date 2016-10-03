package chess;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Upgrade soldier selection panel.
 * @author Sahar
 */
public final class ChessPromotion extends JPanel
{
	private static final long serialVersionUID = -3888253234095447430L;
	
	private final JPanel _cardPanel = new JPanel(new CardLayout());
	
	protected int _oldX;
	protected int _oldY;
	protected int _newX;
	protected int _newY;
	
	public ChessPromotion()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(Box.createVerticalStrut(160));
		
		final JPanel optionsWhite = new JPanel(new GridLayout(1, 4));
		final JPanel optionsBlack = new JPanel(new GridLayout(1, 4));
		final String[] images = {"bishop-", "knight-", "rook-", "queen-"};
		for (int i = 0;i < 4;i++)
		{
			final JButton white = new ChessButton(images[i] + "white", false);
			white.addMouseListener(new MakeChoice(images[i] + "white"));
			white.setBackground(i % 2 == 0 ? Color.GRAY : Color.LIGHT_GRAY);
			final JButton black = new ChessButton(images[i] + "black", false);
			black.addMouseListener(new MakeChoice(images[i] + "black"));
			black.setBackground(i % 2 == 0 ? Color.GRAY : Color.LIGHT_GRAY);
			
			optionsWhite.add(white);
			optionsBlack.add(black);
		}
		_cardPanel.setMaximumSize(new Dimension(400, 100));
		_cardPanel.add(optionsWhite, "white");
		_cardPanel.add(optionsBlack, "black");
		add(_cardPanel);
	}
	
	public void showSelectionWindow(final String color, final int oldX, final int oldY, final int newX, final int newY)
	{
		_oldX = oldX;
		_oldY = oldY;
		_newX = newX;
		_newY = newY;
		
		final CardLayout layout = (CardLayout) _cardPanel.getLayout();
		layout.show(_cardPanel, color);
		
		ChessScreen.getInstance().switchPanels(ChessScreen.PROMOTION);
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		g.drawImage(ChessBackground.CHOOSE, 0, 0, getWidth(), getHeight(), null);
	}
	
	private class MakeChoice extends MouseAdapter
	{
		private final String _image;
		
		protected MakeChoice(final String image)
		{
			_image = image;
		}
		
		@Override
		public void mousePressed(final MouseEvent me)
		{
			ChessScreen.getInstance().getBoard().changeTurnAfterPromotion(_image, _oldX, _oldY, _newX, _newY);
			ChessScreen.getInstance().switchPanels(ChessScreen.BOARD);
		}
	}
}