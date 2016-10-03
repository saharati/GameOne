package chess;

import java.awt.CardLayout;
import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import client.Client;
import network.request.RequestUpdateGameScore;
import network.request.RequestWaitingRoom;
import objects.GameId;
import objects.GameResult;
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
	public static final String IMAGE_PATH = "./images/chess/";
	public static final Map<String, Image> IMAGES = new HashMap<>();
	static
	{
		for (final File file : new File(IMAGE_PATH).listFiles())
			if (file.isFile())
				IMAGES.put(file.getName().substring(0, file.getName().lastIndexOf('.')), new ImageIcon(file.getAbsolutePath()).getImage());
	}
	
	private final CardLayout _layout = new CardLayout();
	private final ChessPromotion _promotionPanel = new ChessPromotion();
	private ChessBoard _board;
	
	protected ChessScreen()
	{
		super("GameOne Client - Chess");
		
		setLayout(_layout);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		add(_promotionPanel, PROMOTION);
		
		LOGGER.info("Chess screen loaded.");
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LEAVE, _board.calcScore()));
		Client.getInstance().setCurrentDetails(WaitingRoom.getInstance(), GameId.CHESS, false);
	}
	
	public void start(final String myColor)
	{
		if (_board != null)
			remove(_board);
		
		_board = new ChessBoard(myColor);
		add(_board, BOARD);
		
		_layout.show(getContentPane(), BOARD);
		
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
				Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.EXIT, _board.calcScore()));
				return;
		}
		
		setVisible(false);
		
		Client.getInstance().setCurrentDetails(WaitingRoom.getInstance(), GameId.CHESS, false);
		Client.getInstance().sendPacket(new RequestWaitingRoom(GameId.CHESS));
	}
	
	public void switchPanels(final String panel)
	{
		_layout.show(getContentPane(), panel);
	}
	
	public ChessBoard getBoard()
	{
		return _board;
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