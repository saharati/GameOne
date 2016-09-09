package util.database.installer.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;
import javax.swing.SwingConstants;

import util.database.installer.MySqlConnect;
import util.database.installer.RunTasks;

/**
 * Connection information window.
 * @author Sahar
 */
public final class DBConfigGUI extends JFrame
{
	private static final long serialVersionUID = -8391792251140797076L;
	
	private final JTextField _dbHost;
	private final JTextField _dbPort;
	private final JTextField _dbUser;
	private final JPasswordField _dbPass;
	private final JTextField _dbName;
	private final Preferences _prop;
	
	public DBConfigGUI()
	{
		super("GameOne Database Installer");
		
		_prop = Preferences.userRoot();
		
		final int width = 260;
		final int height = 220;
		final Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLayout(new SpringLayout());
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds((resolution.width - width) / 2, (resolution.height - height) / 2, width, height);
		setResizable(false);
		
		// Host
		_dbHost = new JTextField(15);
		_dbHost.setText(_prop.get("dbHost_gameOne", "localhost"));
		
		final JLabel labelDbHost = new JLabel("Host: ", SwingConstants.LEFT);
		labelDbHost.setLabelFor(_dbHost);
		
		add(labelDbHost);
		add(_dbHost);
		
		// Port
		_dbPort = new JTextField(15);
		_dbPort.setText(_prop.get("dbPort_gameOne", "3306"));
		
		final JLabel labelDbPort = new JLabel("Port: ", SwingConstants.LEFT);
		labelDbPort.setLabelFor(_dbPort);
		
		add(labelDbPort);
		add(_dbPort);
		
		// Username
		_dbUser = new JTextField(15);
		_dbUser.setText(_prop.get("dbUser_gameOne", "root"));
		
		final JLabel labelDbUser = new JLabel("Username: ", SwingConstants.LEFT);
		labelDbUser.setLabelFor(_dbUser);
		
		add(labelDbUser);
		add(_dbUser);
		
		// Password
		_dbPass = new JPasswordField(15);
		_dbPass.setText(_prop.get("dbPass_gameOne", ""));
		
		final JLabel labelDbPass = new JLabel("Password: ", SwingConstants.LEFT);
		labelDbPass.setLabelFor(_dbPass);
		
		add(labelDbPass);
		add(_dbPass);
		
		// Database
		_dbName = new JTextField(15);
		_dbName.setText(_prop.get("dbName_gameOne", "gameOne"));
		
		final JLabel labelDbName = new JLabel("Database: ", SwingConstants.LEFT);
		labelDbName.setLabelFor(_dbName);
		
		add(labelDbName);
		add(_dbName);
		
		// Cancel
		final ActionListener cancelListener = e -> System.exit(0);
		final JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(cancelListener);
		add(btnCancel);
		
		// Connect
		final ActionListener connectListener = e ->
		{
			final MySqlConnect connector = new MySqlConnect(_dbHost.getText(), _dbPort.getText(), _dbUser.getText(), new String(_dbPass.getPassword()), _dbName.getText(), false);
			if (connector.getConnection() != null)
			{
				_prop.put("dbHost_gameOne", _dbHost.getText());
				_prop.put("dbPort_gameOne", _dbPort.getText());
				_prop.put("dbUser_gameOne", _dbUser.getText());
				_prop.put("dbName_gameOne", _dbName.getText());
				
				boolean cleanInstall = false;
				
				setVisible(false);
				
				final Object[] options = {"Full Install", "Upgrade", "Exit"};
				final int n = JOptionPane.showOptionDialog(null, "Select Installation Type", "Installation Type", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if (n == 2 || n == -1)
					System.exit(0);
				if (n == 0)
				{
					final int conf = JOptionPane.showConfirmDialog(null, "Do you really want to destroy your db?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (conf == 1)
						System.exit(0);
					
					cleanInstall = true;
				}
				
				final DBInstallerGUI dbi = new DBInstallerGUI(connector.getConnection());
				dbi.setVisible(true);
				
				final RunTasks task = new RunTasks(dbi, cleanInstall);
				task.setPriority(Thread.MAX_PRIORITY);
				task.start();
			}
		};
		final JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(connectListener);
		add(btnConnect);
		
		makeCompactGrid(getContentPane(), 6, 2, 5, 5, 5, 5);
		
		setVisible(true);
	}
	
	private static Constraints getConstraintsForCell(final int row, final int col, final Container parent, final int cols)
	{
		final SpringLayout layout = (SpringLayout) parent.getLayout();
		final Component c = parent.getComponent(row * cols + col);
		
		return layout.getConstraints(c);
	}
	
	private static void makeCompactGrid(final Container parent, final int rows, final int cols, final int initialX, final int initialY, final int xPad, final int yPad)
	{
		final SpringLayout layout = (SpringLayout) parent.getLayout();
		
		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0;c < cols;c++)
		{
			Spring width = Spring.constant(0);
			for (int r = 0;r < rows;r++)
				width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
			for (int r = 0;r < rows;r++)
			{
				final Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				
				constraints.setX(x);
				constraints.setWidth(width);
			}
			
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}
		
		// Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0;r < rows;r++)
		{
			Spring height = Spring.constant(0);
			for (int c = 0;c < cols;c++)
				height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
			for (int c = 0; c < cols; c++)
			{
				final Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				
				constraints.setY(y);
				constraints.setHeight(height);
			}
			
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}
		
		// Set the parent's size.
		final Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}
}