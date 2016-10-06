package chess;

import java.awt.CardLayout;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import client.Client;
import network.request.RequestUpdateGameScore;
import network.request.RequestWaitingRoom;
import objects.GameId;
import objects.GameResult;
import windows.GameSelect;
import windows.WaitingRoom;

/**
 * Chess main frame.
 * @author Sahar
 */
public final class ChessScreen extends JFrame
{
	private static final long serialVersionUID = -7018309977842760132L;
	private static final Logger LOGGER = Logger.getLogger(ChessScreen.class.getName());
	
	public static final String PROMOTION = "promotion";
	public static final String BOARD = "board";
	
	private final CardLayout _layout = new CardLayout();
	private final ChessPromotion _promotionPanel = new ChessPromotion();
	
	protected ChessScreen()
	{
		super("GameOne Client - Chess");
		
		setLayout(_layout);
		
		add(ChessBoard.getInstance(), BOARD);
		add(_promotionPanel, PROMOTION);
		
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		LOGGER.info("Chess screen loaded.");
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		if (ChessBoard.getInstance().isSingleplayer())
			Client.getInstance().setCurrentDetails(GameSelect.getInstance(), null, true);
		else
		{
			Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LEAVE, ChessBoard.getInstance().calcScore()));
			Client.getInstance().setCurrentDetails(WaitingRoom.getInstance(), GameId.CHESS_MP, false);
		}
	}
	
	public void start(final String myColor, final boolean sp)
	{
		_layout.show(getContentPane(), BOARD);
		
		ChessBoard.getInstance().start(myColor, sp);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void showResult(final GameResult result)
	{
		switch (result)
		{
			case WIN:
				ChessBackground.getInstance().showDialog("Checkmate", ChessBackground.WON);
				break;
			case TIE:
				ChessBackground.getInstance().showDialog("Tie", ChessBackground.TIE);
				break;
			case LEAVE:
				ChessBackground.getInstance().showDialog("Victory", ChessBackground.OFF);
				break;
			case EXIT:
				Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.EXIT, ChessBoard.getInstance().calcScore()));
				return;
		}
		
		setVisible(false);
		
		Client.getInstance().setCurrentDetails(WaitingRoom.getInstance(), GameId.CHESS_MP, false);
		Client.getInstance().sendPacket(new RequestWaitingRoom(GameId.CHESS_MP));
	}
	
	public void switchPanel(final String switchTo)
	{
		_layout.show(getContentPane(), switchTo);
	}
	
	public ChessPromotion getPromotionPanel()
	{
		return _promotionPanel;
	}
	
	public static ChessScreen getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ChessScreen INSTANCE = new ChessScreen();
	}
}