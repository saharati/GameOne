package util.database.installer;

import javax.swing.UIManager;

import util.database.installer.console.DBInstallerConsole;
import util.database.installer.gui.DBConfigGUI;

/**
 * Contains main class for Database Installer If system doesn't support the graphical UI, start the installer in console mode.
 * @author Sahar
 */
public final class Launcher
{
	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			new DBConfigGUI();
		}
		catch (final Exception e)
		{
			new DBInstallerConsole();
		}
	}
}