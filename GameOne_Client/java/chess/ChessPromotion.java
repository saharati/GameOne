package chess;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Upgrade soldier selection panel.
 * @author Sahar
 */
public final class ChessPromotion extends JPanel
{
	private static final long serialVersionUID = -3888253234095447430L;
	
	private final JPanel _cardPanel = new JPanel(new CardLayout());
	
	private int _oldX;
	private int _oldY;
	private int _newX;
	private int _newY;
	
	public ChessPromotion()
	{
		super(new BorderLayout());
		
		final JLabel chooseUpgrade = new JLabel("Choose an upgrade");
		final Font titleFont = new Font("Arial", Font.BOLD, 25);
		chooseUpgrade.setFont(titleFont);
		chooseUpgrade.setHorizontalAlignment(JLabel.CENTER);
		chooseUpgrade.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(chooseUpgrade, BorderLayout.PAGE_START);
		
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
		_cardPanel.add(optionsWhite, "white");
		_cardPanel.add(optionsBlack, "black");
		add(_cardPanel, BorderLayout.PAGE_END);
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
	
	private class MakeChoice extends MouseAdapter
	{
		private final String _image;
		
		private MakeChoice(final String image)
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