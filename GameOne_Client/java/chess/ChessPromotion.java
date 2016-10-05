package chess;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import chess.objects.AbstractObject;
import chess.objects.Bishop;
import chess.objects.Knight;
import chess.objects.Queen;
import chess.objects.Rook;

/**
 * Upgrade soldier selection panel.
 * @author Sahar
 */
public final class ChessPromotion extends JPanel
{
	private static final long serialVersionUID = -3888253234095447430L;
	private static final Logger LOGGER = Logger.getLogger(ChessPromotion.class.getName());
	
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
		final Class<?>[] classes = {Queen.class, Rook.class, Bishop.class, Knight.class};
		for (int i = 0;i < 4;i++)
		{
			try
			{
				final ChessCell white = new ChessCell(0, 0, i % 2 == 0);
				white.setObject((AbstractObject) classes[i].getConstructors()[0].newInstance(0, 0, "white"), false);
				white.addMouseListener(new MakeChoice(white.getObject()));
				final ChessCell black = new ChessCell(0, 0, i % 2 == 0);
				black.setObject((AbstractObject) classes[i].getConstructors()[0].newInstance(0, 0, "black"), false);
				black.addMouseListener(new MakeChoice(black.getObject()));
				
				optionsWhite.add(white);
				optionsBlack.add(black);
			}
			catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e)
			{
				LOGGER.log(Level.WARNING, "Failed initializing ChessPromotion: ", e);
			}
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
		
		ChessScreen.getInstance().switchPanel(ChessScreen.PROMOTION);
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		g.drawImage(ChessBackground.CHOOSE, 0, 0, getWidth(), getHeight(), null);
	}
	
	private class MakeChoice extends MouseAdapter
	{
		private final AbstractObject _object;
		
		protected MakeChoice(final AbstractObject object)
		{
			_object = object;
		}
		
		@Override
		public void mousePressed(final MouseEvent me)
		{
			ChessBoard.getInstance().changeTurnAfterPromotion(_object, _oldX, _oldY, _newX, _newY);
			ChessScreen.getInstance().switchPanel(ChessScreen.BOARD);
		}
	}
}