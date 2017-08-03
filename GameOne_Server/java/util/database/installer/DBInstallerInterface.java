package util.database.installer;

import java.sql.Connection;

public interface DBInstallerInterface
{
	public default void setProgressIndeterminate(final boolean value)
	{
		// For GUI only.
	}
	
	public default void setProgressMaximum(final int maxValue)
	{
		// For GUI only.
	}
	
	public default void setProgressValue(final int value)
	{
		// For GUI only.
	}
	
	public default void setFrameVisible(final boolean value)
	{
		// For GUI only.
	}
	
	public Connection getConnection();
	
	public void appendToProgressArea(final String text);
	
	public void showMessage(final String title, final String message, final int type);
	
	public int requestConfirm(final String title, final String message, final int type);
}