package windows;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import client.Client;
import configs.Config;
import mario.SuperMario;
import mario.MarioTaskManager;
import network.ConnectionManager;
import network.request.RequestGameObjects;
import objects.GameId;
import pacman.PacmanBuilder;
import util.StringUtil;
import util.configs.CommonConfig;
import util.parsers.xml.XmlFactory;
import util.threadpool.ThreadPool;

/**
 * This class loads client info and then connects to server.
 * @author Sahar
 */
public final class Startup extends JFrame
{
	private static final long serialVersionUID = 4258564261972919749L;
	
	private final JLabel _currentLoad = new JLabel("GameOne Client - Loading...");
	private final JProgressBar _progressBar = new JProgressBar();
	
	private Startup() throws SecurityException, URISyntaxException, IOException
	{
		super("GameOne Client - Sahar Atias");
		
		CommonConfig.load();
		Config.load();
		
		final Font font = new Font("Arial", Font.BOLD, 20);
		_currentLoad.setBounds(5, 5, 300, 30);
		_currentLoad.setHorizontalAlignment(JLabel.CENTER);
		_currentLoad.setFont(font);
		add(_currentLoad);
		_progressBar.setBounds(5, 40, 300, 30);
		_progressBar.setFont(font);
		_progressBar.setMaximum(5);
		add(_progressBar);
		
		getRootPane().setBorder(BorderFactory.createLineBorder(Color.RED));
		getContentPane().setBackground(Config.UI_COLOR);
		setUndecorated(true);
		setLayout(null);
		setResizable(false);
		setSize(312, 77);
		setLocationRelativeTo(null);
		setVisible(true);
		
		// Network
		_currentLoad.setText("Connecting to Server...");
		
		StringUtil.printSection("Network");
		ConnectionManager.open();
		
		_progressBar.setValue(1);
		
		// XMLs
		_currentLoad.setText("Initializing XMLs...");
		
		StringUtil.printSection("Parsers");
		XmlFactory.load();
		
		_progressBar.setValue(2);
		
		// ThreadPool
		_currentLoad.setText("Initializing ThreadPools...");
		
		StringUtil.printSection("ThreadPool");
		ThreadPool.load();
		MarioTaskManager.getInstance();
		
		_progressBar.setValue(3);
		
		// Objects
		_currentLoad.setText("Loading Objects...");
		
		StringUtil.printSection("Objects");
		Client.getInstance().setStartupWindow(this);
		Client.getInstance().sendPacket(new RequestGameObjects(GameId.MARIO));
		Client.getInstance().sendPacket(new RequestGameObjects(GameId.PACMAN));
		
		_progressBar.setValue(4);
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
		
		_progressBar.setValue(5);
		
		Client.getInstance().setCurrentWindow(Login.getInstance());
	}
	
	public static void main(final String[] args) throws SecurityException, URISyntaxException, IOException
	{
		new Startup();
	}
}