package windows;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.xml.parsers.ParserConfigurationException;

import checkers.CheckersScreen;
import chess.ChessBackground;
import chess.ChessBoard;
import chess.ChessScreen;
import client.Client;
import configs.Config;
import mario.SuperMario;
import mario.MarioTaskManager;
import network.ConnectionManager;
import network.request.RequestGameConfigs;
import network.request.RequestGameObjects;
import objects.GameId;
import pacman.PacmanBuilder;
import s2048.S2048;
import sal.SalScreen;
import snake.SnakeScreen;
import tetris.TetrisScreen;
import util.ComponentUtil;
import util.StringUtil;
import util.configs.CommonConfig;
import util.parsers.XmlFactory;
import util.threadpool.ThreadPool;

public final class Startup extends JFrame
{
	private static final long serialVersionUID = 4258564261972919749L;
	private static final Logger LOGGER = Logger.getLogger(Startup.class.getName());
	
	private final JLabel _currentLoad = new JLabel("GameOne Client - Loading...");
	private final JProgressBar _progressBar = new JProgressBar();
	
	private Startup()
	{
		super("GameOne Client - Sahar Atias");
		
		final Font font = new Font("Arial", Font.BOLD, 20);
		_currentLoad.setBounds(5, 5, 300, 30);
		_currentLoad.setHorizontalAlignment(SwingConstants.CENTER);
		_currentLoad.setFont(font);
		add(_currentLoad);
		_progressBar.setBounds(5, 40, 300, 30);
		_progressBar.setFont(font);
		add(_progressBar);
		
		getRootPane().setBorder(BorderFactory.createLineBorder(Color.RED));
		getContentPane().setBackground(Config.UI_COLOR);
		setUndecorated(true);
		setLayout(null);
		setResizable(false);
		setSize(312, 77);
		setLocationRelativeTo(null);
		setVisible(true);
		
		// Configuration
		_currentLoad.setText("Loading Configuration");
		try
		{
			CommonConfig.load();
		}
		catch (final SecurityException | URISyntaxException | IOException e)
		{
			LOGGER.log(Level.SEVERE, "Failed reading Log.properties: ", e);
			
			ComponentUtil.showHyperLinkPopup("Initialize Error", "<html><body>Could not read file Log.properties.<br>Please make sure it exists and that it is readable.<br>For support open an issue at <a href=\"https://github.com/saharati/GameOne\">https://github.com/saharati/GameOne</a>.</body></html>", JOptionPane.ERROR_MESSAGE);
			
			System.exit(0);
		}
		Config.load();
		try
		{
			XmlFactory.load();
		}
		catch (final ParserConfigurationException e)
		{
			LOGGER.log(Level.SEVERE, "Failed initializing XML Factory: ", e);
			
			ComponentUtil.showHyperLinkPopup("Initialize Error", "<html><body>Could not start XML Factory.<br>Please check your log file and open a ticket at:<br><a href=\"https://github.com/saharati/GameOne\">https://github.com/saharati/GameOne</a></body></html>", JOptionPane.ERROR_MESSAGE);
			
			System.exit(0);
		}
		_progressBar.setValue(20);
		
		// Network
		_currentLoad.setText("Connecting to Server...");
		StringUtil.printSection("Network");
		ConnectionManager.open();
		_progressBar.setValue(40);
		
		// ThreadPool
		_currentLoad.setText("Initializing ThreadPool...");
		StringUtil.printSection("ThreadPool");
		ThreadPool.load();
		MarioTaskManager.getInstance();
		_progressBar.setValue(60);
		
		// Objects
		_currentLoad.setText("Loading Objects...");
		StringUtil.printSection("Objects");
		Client.getInstance().setCurrentDetails(this, null, false);
		Client.getInstance().sendPacket(RequestGameConfigs.STATIC_PACKET);
		Client.getInstance().sendPacket(new RequestGameObjects(GameId.MARIO));
		Client.getInstance().sendPacket(new RequestGameObjects(GameId.PACMAN));
		_progressBar.setValue(80);
	}
	
	public void progress()
	{
		// Windows
		_currentLoad.setText("Initializing Windows...");
		StringUtil.printSection("Windows");
		Login.getInstance();
		GameSelect.getInstance();
		PacmanBuilder.getInstance();
		SuperMario.getInstance();
		TetrisScreen.getInstance();
		SnakeScreen.getInstance();
		S2048.getInstance();
		WaitingRoom.getInstance();
		ChessScreen.getInstance();
		SalScreen.getInstance();
		CheckersScreen.getInstance();
		ChessBackground.getInstance();
		ChessBoard.getInstance();
		_progressBar.setValue(100);
		
		// Everything loaded successfully, move to login screen.
		Client.getInstance().getCurrentWindow().setVisible(false);
		Client.getInstance().setCurrentDetails(Login.getInstance(), null, false);
		Client.getInstance().getCurrentWindow().setVisible(true);
	}
	
	public static void main(final String[] args)
	{
		new Startup();
	}
}