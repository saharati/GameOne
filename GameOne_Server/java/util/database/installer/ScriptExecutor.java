package util.database.installer;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JOptionPane;

public final class ScriptExecutor implements FileFilter
{
	private final DBInstallerInterface _frame;
	
	public ScriptExecutor(final DBInstallerInterface frame)
	{
		_frame = frame;
	}
	
	public void execSqlBatch(final File dir, final boolean skipErrors)
	{
		final File[] file = dir.listFiles(this);
		
		Arrays.sort(file);
		
		_frame.setProgressIndeterminate(false);
		_frame.setProgressMaximum(file.length - 1);
		
		for (int i = 0;i < file.length;i++)
		{
			_frame.setProgressValue(i);
			
			execSqlFile(file[i], skipErrors);
		}
	}
	
	@SuppressWarnings("resource")
	public void execSqlFile(final File file, final boolean skipErrors)
	{
		_frame.appendToProgressArea("Installing " + file.getName());
		
		final Connection con = _frame.getConnection();
		try (final Statement stmt = con.createStatement();
			final Scanner scn = new Scanner(file))
		{
			StringBuilder sb = new StringBuilder();
			while (scn.hasNextLine())
			{
				String line = scn.nextLine();
				if (line.startsWith("--"))
					continue;
				
				if (line.contains("--"))
					line = line.split("--")[0];
				
				line = line.trim();
				if (!line.isEmpty())
					sb.append(line + System.getProperty("line.separator"));
				
				if (line.endsWith(";"))
				{
					stmt.execute(sb.toString());
					
					sb = new StringBuilder();
				}
			}
		}
		catch (final FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, "File Not Found: " + e.getMessage(), "Installer Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (final SQLException e)
		{
			if (!skipErrors)
			{
				try
				{
					final Object[] options = {"Continue", "Abort"};
					final int n = JOptionPane.showOptionDialog(null, "MySQL Error: " + e.getMessage(), "Script Error", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
					if (n == 1)
						System.exit(0);
				}
				catch (final HeadlessException h)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("resource")
	public void createDump()
	{
		try (final Formatter form = new Formatter())
		{
			final Connection con = _frame.getConnection();
			try (final Statement s = con.createStatement();
				final ResultSet rset = s.executeQuery("SHOW TABLES"))
			{
				final File dir = new File("./log/dumps");
				dir.mkdirs();
				
				final File dump = new File("./log/dumps", form.format("gameOne_dump_%1$tY%1$tm%1$td-%1$tH%1$tM%1$tS.sql", Calendar.getInstance().getTime()).toString());
				dump.createNewFile();
				
				_frame.appendToProgressArea("Writing dump " + dump.getName());
				if (rset.last())
				{
					final int rows = rset.getRow();
					rset.beforeFirst();
					if (rows > 0)
					{
						_frame.setProgressIndeterminate(false);
						_frame.setProgressMaximum(rows);
					}
				}
				
				try (final FileWriter fileWriter = new FileWriter(dump);
					final FileWriterStdout fws = new FileWriterStdout(fileWriter))
				{
					while (rset.next())
					{
						_frame.setProgressValue(rset.getRow());
						_frame.appendToProgressArea("Dumping Table " + rset.getString(1));
						
						fws.println("CREATE TABLE `" + rset.getString(1) + "`");
						fws.println("(");
						
						try (final Statement desc = con.createStatement();
							final ResultSet dset = desc.executeQuery("DESC " + rset.getString(1)))
						{
							final Map<String, List<String>> keys = new HashMap<>();
							
							boolean isFirst = true;
							while (dset.next())
							{
								if (!isFirst)
									fws.println(",");
								
								fws.print("\t`" + dset.getString(1) + "`");
								fws.print(" " + dset.getString(2));
								if (dset.getString(3).equals("NO"))
									fws.print(" NOT NULL");
								if (!dset.getString(4).isEmpty())
								{
									if (!keys.containsKey(dset.getString(4)))
										keys.put(dset.getString(4), new ArrayList<>());
									
									keys.get(dset.getString(4)).add(dset.getString(1));
								}
								if (dset.getString(5) != null)
									fws.print(" DEFAULT '" + dset.getString(5) + "'");
								if (!dset.getString(6).isEmpty())
									fws.print(" " + dset.getString(6));
								
								isFirst = false;
							}
							if (keys.containsKey("PRI"))
							{
								fws.println(",");
								fws.print("\tPRIMARY KEY (");
								
								isFirst = true;
								for (final String key : keys.get("PRI"))
								{
									if (!isFirst)
										fws.print(", ");
									
									fws.print("`" + key + "`");
									isFirst = false;
								}
								fws.print(")");
							}
							if (keys.containsKey("MUL"))
							{
								fws.println(",");
								
								isFirst = true;
								for (final String key : keys.get("MUL"))
								{
									if (!isFirst)
										fws.println(", ");
									
									fws.print("\tKEY `key_" + key + "` (`" + key + "`)");
									isFirst = false;
								}
							}
							
							fws.println();
							fws.println(");");
							fws.flush();
						}
						
						try (final Statement desc = con.createStatement();
							final ResultSet dset = desc.executeQuery("SELECT * FROM " + rset.getString(1)))
						{
							boolean isFirst = true;
							int cnt = 0;
							while (dset.next())
							{
								if (cnt % 100 == 0)
									fws.println("INSERT INTO `" + rset.getString(1) + "` VALUES ");
								else
									fws.println(",");
								
								fws.print("\t(");
								
								boolean isInFirst = true;
								for (int i = 1;i <= dset.getMetaData().getColumnCount();i++)
								{
									if (!isInFirst)
										fws.print(", ");
									
									if (dset.getString(i) == null)
										fws.print("NULL");
									else
										fws.print("'" + dset.getString(i).replace("\'", "\\\'") + "'");
									
									isInFirst = false;
								}
								
								fws.print(")");
								isFirst = false;
								
								if (cnt % 100 == 99)
									fws.println(";");
								
								cnt++;
							}
							
							if (!isFirst && cnt % 100 != 0)
								fws.println(";");
							
							fws.println();
							fws.flush();
						}
					}
					
					fws.flush();
				}
			}
		}
		catch (final IOException | SQLException e)
		{
			e.printStackTrace();
		}
		
		_frame.appendToProgressArea("Dump Complete!");
	}
	
	@Override
	public boolean accept(final File f)
	{
		if (f == null || !f.isFile())
			return false;
		
		return f.getName().toLowerCase().endsWith(".sql");
	}
}