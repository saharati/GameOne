package windows;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import client.Client;
import configs.Config;
import network.ConnectionManager;
import network.request.RequestGameObjects;
import objects.GameId;
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
	
	private Startup() throws SecurityException, URISyntaxException, IOException
	{
		super("GameOne Client - Sahar Atias");
		
		CommonConfig.load();
		Config.load();
		
		final Font font = new Font("Arial", Font.BOLD, 20);
		final JLabel currentLoad = new JLabel("GameOne Client - Loading...");
		currentLoad.setBounds(5, 5, 300, 30);
		currentLoad.setHorizontalAlignment(JLabel.CENTER);
		currentLoad.setFont(font);
		add(currentLoad);
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(5, 40, 300, 30);
		progressBar.setFont(font);
		progressBar.setMaximum(5);
		add(progressBar);
		
		getRootPane().setBorder(BorderFactory.createLineBorder(Color.RED));
		getContentPane().setBackground(Config.UI_COLOR);
		setUndecorated(true);
		setLayout(null);
		setSize(312, 77);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		
		// Network
		currentLoad.setText("Connecting to Server...");
		
		StringUtil.printSection("Network");
		ConnectionManager.open();
		
		progressBar.setValue(1);
		
		// XMLs
		currentLoad.setText("Initializing XMLs...");
		
		StringUtil.printSection("Parsers");
		XmlFactory.load();
		
		progressBar.setValue(2);
		
		// ThreadPool
		currentLoad.setText("Initializing ThreadPools...");
		
		StringUtil.printSection("ThreadPool");
		ThreadPool.load();
		
		progressBar.setValue(3);
		
		// Windows
		currentLoad.setText("Initializing Windows...");
		
		StringUtil.printSection("Windows");
		Login.getInstance();
		GameSelect.getInstance();
		
		progressBar.setValue(4);
		
		// Objects
		currentLoad.setText("Loading Objects...");
		
		StringUtil.printSection("Objects");
		Client.getInstance().setStartupWindow(this);
		Client.getInstance().sendPacket(new RequestGameObjects(GameId.MARIO));
		Client.getInstance().sendPacket(new RequestGameObjects(GameId.PACMAN));
		
		progressBar.setValue(5);
	}
	
	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, SecurityException, URISyntaxException, IOException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		new Startup();
	}
}