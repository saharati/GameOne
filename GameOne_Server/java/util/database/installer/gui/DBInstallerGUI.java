package util.database.installer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import util.database.installer.DBInstallerInterface;

/**
 * GUI implementation.
 * @author Sahar
 */
public final class DBInstallerGUI extends JFrame implements DBInstallerInterface
{
	private static final long serialVersionUID = -1005504757826370170L;
	
	private final JProgressBar _progBar;
	private final JTextArea _progArea;
	private final Connection _con;
	
	public DBInstallerGUI(final Connection con)
	{
		super("GameOne Database Installer");
		
		_con = con;
		
		final int width = 480;
		final int height = 360;
		final Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLayout(new BorderLayout());
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds((resolution.width - width) / 2, (resolution.height - height) / 2, width, height);
		setResizable(false);
		
		_progBar = new JProgressBar();
		_progBar.setIndeterminate(true);
		add(_progBar, BorderLayout.PAGE_START);
		
		_progArea = new JTextArea();
		
		final JScrollPane scrollPane = new JScrollPane(_progArea);
		
		_progArea.setEditable(false);
		appendToProgressArea("Connected");
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	@Override
	public void setProgressIndeterminate(final boolean value)
	{
		_progBar.setIndeterminate(value);
	}
	
	@Override
	public void setProgressMaximum(final int maxValue)
	{
		_progBar.setMaximum(maxValue);
	}
	
	@Override
	public void setProgressValue(final int value)
	{
		_progBar.setValue(value);
	}
	
	@Override
	public void setFrameVisible(final boolean value)
	{
		setVisible(value);
	}
	
	@Override
	public Connection getConnection()
	{
		return _con;
	}
	
	@Override
	public void appendToProgressArea(final String text)
	{
		_progArea.append(text + System.getProperty("line.separator"));
		_progArea.setCaretPosition(_progArea.getDocument().getLength());
	}
	
	@Override
	public void showMessage(final String title, final String message, final int type)
	{
		JOptionPane.showMessageDialog(null, message, title, type);
	}
	
	@Override
	public int requestConfirm(final String title, final String message, final int type)
	{
		return JOptionPane.showConfirmDialog(null, message, title, type);
	}
}