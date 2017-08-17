package windows;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import client.Client;
import configs.Config;
import network.request.RequestLogin;
import util.ComponentUtil;

public final class Login extends JFrame
{
	private static final Logger LOGGER = Logger.getLogger(Login.class.getName());
	private static final long serialVersionUID = 5594699723429753388L;
	
	private final JTextField _username;
	private final JPasswordField _password;
	
	protected Login()
	{
		super("GameOne Client - Sahar Atias");
		
		final Preferences prop = Preferences.userRoot();
		final KeyboardLoginListener enterListener = new KeyboardLoginListener();
		
		// Username
		_username = new JTextField(15);
		_username.setText(prop.get("user_gameOne", ""));
		_username.addKeyListener(enterListener);
		
		final JLabel labelUsername = new JLabel("Username: ", SwingConstants.LEFT);
		labelUsername.setLabelFor(_username);
		
		add(labelUsername);
		add(_username);
		
		// Password
		_password = new JPasswordField(15);
		_password.setText(prop.get("pass_gameOne", ""));
		_password.addKeyListener(enterListener);
		
		final JLabel labelPassword = new JLabel("Password: ", SwingConstants.LEFT);
		labelPassword.setLabelFor(_password);
		
		add(labelPassword);
		add(_password);
		
		// Cancel
		final ActionListener cancelListener = e -> System.exit(0);
		final JButton btnCancel = new JButton("Exit");
		btnCancel.addActionListener(cancelListener);
		add(btnCancel);
		
		// Connect
		final ActionListener connectListener = e -> submitForm();
		final JButton btnConnect = new JButton("Login");
		btnConnect.addActionListener(connectListener);
		add(btnConnect);
		
		setLayout(new SpringLayout());
		
		ComponentUtil.makeCompactGrid(getContentPane(), 3, 2, 5, 5, 5, 5);
		
		getContentPane().setBackground(Config.UI_COLOR);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		
		LOGGER.info("Login screen loaded.");
	}
	
	protected void submitForm()
	{
		if (_username.getText().isEmpty())
			JOptionPane.showMessageDialog(null, "Please enter your username.", "Login Failed", JOptionPane.ERROR_MESSAGE);
		else if (_username.getText().contains(" "))
			JOptionPane.showMessageDialog(null, "Usernames are not allowed to have spaces.", "Login Failed", JOptionPane.ERROR_MESSAGE);
		else if (_password.getPassword().length == 0)
			JOptionPane.showMessageDialog(null, "Please enter your password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
		else
		{
			try
			{
				final String username = _username.getText();
				final String password = new String(_password.getPassword());
				
				Client.getInstance().setLoginDetails(username, password);
				
				final MessageDigest md = MessageDigest.getInstance("SHA");
				final byte[] raw = password.getBytes(StandardCharsets.UTF_8);
				final String hashed = Base64.getEncoder().encodeToString(md.digest(raw));
				final byte[] macBytes = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
				final StringBuilder sb = new StringBuilder();
				for (int i = 0;i < macBytes.length;i++)
					sb.append(String.format("%02X%s", macBytes[i], (i < macBytes.length - 1) ? "-" : ""));
				final String mac = sb.toString();
				final RequestLogin loginPacket = new RequestLogin(username, hashed, mac);
				
				Client.getInstance().sendPacket(loginPacket);
			}
			catch (final NoSuchAlgorithmException e)
			{
				JOptionPane.showMessageDialog(null, "Password encryption failed.", "Login Failed", JOptionPane.ERROR_MESSAGE);
			}
			catch (final SocketException | UnknownHostException e)
			{
				JOptionPane.showMessageDialog(null, "A network error has occoured.", "Login Failed", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
 
	protected class KeyboardLoginListener extends KeyAdapter
	{
		@Override
		public void keyTyped(final KeyEvent ke)
		{
			if (ke.getKeyChar() == KeyEvent.VK_ENTER)
				submitForm();
		}
	}
	
	public static Login getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final Login INSTANCE = new Login();
	}
}