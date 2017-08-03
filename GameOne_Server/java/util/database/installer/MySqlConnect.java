package util.database.installer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import javax.swing.JOptionPane;

public final class MySqlConnect
{
	private Connection _con;
	
	public MySqlConnect(final String host, final String port, final String user, final String password, final String db, final boolean console)
	{
		try (final Formatter form = new Formatter())
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			final String formattedText = form.format("jdbc:mysql://%1$s:%2$s", host, port).toString();
			_con = DriverManager.getConnection(formattedText, user, password);
			
			try (final Statement s = _con.createStatement())
			{
				s.execute("CREATE DATABASE IF NOT EXISTS `" + db + "`");
				s.execute("USE `" + db + "`");
			}
		}
		catch (final SQLException e)
		{
			if (console)
			{
				e.printStackTrace();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "MySQL Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			if (console)
			{
				e.printStackTrace();
			}
			else
			{
				JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public Connection getConnection()
	{
		return _con;
	}
}