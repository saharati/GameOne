package util.database.installer;

import javax.swing.JFrame;
import javax.swing.UIManager;

import util.database.installer.console.DBInstallerConsole;
import util.database.installer.gui.DBConfigGUI;

public final class Launcher
{
	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JFrame.setDefaultLookAndFeelDecorated(true);
			
			new DBConfigGUI();
		}
		catch (final Exception e)
		{
			new DBInstallerConsole();
		}
	}
}