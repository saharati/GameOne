package util.database.installer;

import java.io.File;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public final class RunTasks extends Thread
{
	private final DBInstallerInterface _frame;
	private final boolean _cleanInstall;
	
	public RunTasks(final DBInstallerInterface frame, final boolean cleanInstall)
	{
		_frame = frame;
		_cleanInstall = cleanInstall;
	}
	
	@Override
	public void run()
	{
		final ScriptExecutor exec = new ScriptExecutor(_frame);
		final File clnFile = new File("./sql/clean/cleanup.sql");
		final File sqlDir = new File("./sql/");
		
		exec.createDump();
		
		if (_cleanInstall)
		{
			if (clnFile.exists())
			{
				_frame.appendToProgressArea("Cleaning Database...");
				exec.execSqlFile(clnFile, false);
				_frame.appendToProgressArea("Database Cleaned!");
			}
			else
				_frame.appendToProgressArea("Database Cleaning Script Not Found!");
		}
		
		_frame.appendToProgressArea("Installing Database Content...");
		exec.execSqlBatch(sqlDir, false);
		_frame.appendToProgressArea("Database Installation Complete!");
		
		try
		{
			_frame.getConnection().close();
		}
		catch (final SQLException e)
		{
			_frame.showMessage("Connection Error", "Cannot close MySQL Connection: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
		
		_frame.setFrameVisible(false);
		_frame.showMessage("Done!", "Database Installation Complete!", JOptionPane.INFORMATION_MESSAGE);
		
		System.exit(0);
	}
}