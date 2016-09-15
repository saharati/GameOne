package windows;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;

import client.Client;
import client.network.outgoing.RequestGame;
import client.network.outgoing.RequestLogout;
import client.network.outgoing.RequestMessage;
import configs.Config;
import gui.SpringUtilities;
import objects.GameId;
import util.LengthDocumentFilter;

/**
 * Game select window.
 * @author Sahar
 */
public final class GameSelect extends JFrame
{
	private static final long serialVersionUID = 4663554734276386234L;
	private static final Logger LOGGER = Logger.getLogger(GameSelect.class.getSimpleName());
	private static final JButton DUMMY = new JButton();
	static
	{
		DUMMY.setVisible(false);
	}
	
	private JTextField _sender = new JTextField(20);
	private JTextArea _chat = new JTextArea(10, 10);
	
	private GameSelect()
	{
		super("GameOne Client - Sahar Atias");
		
		// Set some padding.
		final JPanel contentPanel = new JPanel();
		final Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		contentPanel.setBorder(padding);
		setContentPane(contentPanel);
		
		// Set layout to BorderLayout.
		setLayout(new BorderLayout());
		
		// Create the page title first.
		final JLabel selectGame = new JLabel("Choose Your Game!");
		final Font titleFont = new Font("Arial", Font.BOLD, 25);
		selectGame.setFont(titleFont);
		selectGame.setHorizontalAlignment(JLabel.CENTER);
		add(selectGame, BorderLayout.PAGE_START);
		
		// Create the buttons panel next, using SpringLayout.
		final JPanel buttonsPanel = new JPanel(new SpringLayout());
		final JButton spPacman = new JButton("Pacman (SP)");
		spPacman.addMouseListener(new MouseGameSelectListener(GameId.PACMAN));
		buttonsPanel.add(spPacman);
		final JButton mpChess = new JButton("Chess (MP)");
		mpChess.addMouseListener(new MouseGameSelectListener(GameId.CHESS));
		buttonsPanel.add(mpChess);
		final JButton spTetris = new JButton("Tetris (SP)");
		spTetris.addMouseListener(new MouseGameSelectListener(GameId.TETRIS));
		buttonsPanel.add(spTetris);
		final JButton mpSal = new JButton("Slide a Lama (MP)");
		mpSal.addMouseListener(new MouseGameSelectListener(GameId.LAMA));
		buttonsPanel.add(mpSal);
		final JButton spSnake = new JButton("Snake (SP)");
		spSnake.addMouseListener(new MouseGameSelectListener(GameId.SNAKE));
		buttonsPanel.add(spSnake);
		final JButton mpCheckers = new JButton("Checkers (MP)");
		mpCheckers.addMouseListener(new MouseGameSelectListener(GameId.CHECKERS));
		buttonsPanel.add(mpCheckers);
		final JButton mario = new JButton("Super Mario (SP)");
		mario.addMouseListener(new MouseGameSelectListener(GameId.MARIO));
		buttonsPanel.add(mario);
		final JButton s2048 = new JButton("2048 (SP)");
		s2048.addMouseListener(new MouseGameSelectListener(GameId.G2048));
		buttonsPanel.add(s2048);
		buttonsPanel.add(DUMMY);
		
		SpringUtilities.makeCompactGrid(buttonsPanel, 3, 3, 10, 10, 10, 10);
		
		buttonsPanel.setBackground(Config.UI_COLOR);
		add(buttonsPanel, BorderLayout.CENTER);
		
		// Finally, create the chat panel, also using BorderLayout.
		final JPanel chatPanel = new JPanel(new BorderLayout(5, 5));
		final Font chatFont = new Font("Arial", Font.BOLD, 15);
		final JScrollPane chat = new JScrollPane(_chat);
		_chat.setFont(chatFont);
		_chat.setLineWrap(true);
		_chat.setWrapStyleWord(true);
		_chat.setEditable(false);
		_chat.setBorder(LineBorder.createBlackLineBorder());
		chatPanel.add(chat, BorderLayout.PAGE_START);
		_sender.setBorder(LineBorder.createBlackLineBorder());
		((AbstractDocument) _sender.getDocument()).setDocumentFilter(new LengthDocumentFilter(40));
		_sender.addKeyListener(new KeyboardChatSendListener());
		chatPanel.add(_sender, BorderLayout.CENTER);
		final JButton send = new JButton("Send");
		send.addActionListener(e -> sendText());
		chatPanel.add(send, BorderLayout.EAST);
		final JButton logout = new JButton("Logout");
		logout.addActionListener(e -> Client.getInstance().sendPacket(RequestLogout.STATIC_PACKET));
		chatPanel.add(logout, BorderLayout.WEST);
		chatPanel.setBackground(Config.UI_COLOR);
		add(chatPanel, BorderLayout.PAGE_END);
		
		getContentPane().setBackground(Config.UI_COLOR);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		
		LOGGER.info("GameSelect screen loaded.");
	}
	
	public JTextArea getChatWindow()
	{
		return _chat;
	}
	
	public JTextField getSender()
	{
		return _sender;
	}
	
	private void sendText()
	{
		if (_sender.getText().trim().isEmpty())
			return;
		
		final RequestMessage msg = new RequestMessage(_sender.getText());
		Client.getInstance().sendPacket(msg);
		
		_sender.setText("");
	}
	
	private class MouseGameSelectListener extends MouseAdapter
	{
		private final GameId _gameId;
		
		private MouseGameSelectListener(final GameId gameId)
		{
			_gameId = gameId;
		}
		
		@Override
		public void mousePressed(final MouseEvent me)
		{
			Client.getInstance().sendPacket(new RequestGame(_gameId));
		}
	}
	
	private class KeyboardChatSendListener extends KeyAdapter
	{
		@Override
		public void keyTyped(final KeyEvent ke)
		{
			if (ke.getKeyChar() == KeyEvent.VK_ENTER)
				sendText();
		}
	}
	
	public static GameSelect getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final GameSelect INSTANCE = new GameSelect();
	}
}