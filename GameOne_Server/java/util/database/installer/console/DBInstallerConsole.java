/*
 * Copyright (C) 2004-2015 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package util.database.installer.console;

import java.sql.Connection;
import java.util.Scanner;
import java.util.prefs.Preferences;

import util.database.installer.DBInstallerInterface;
import util.database.installer.MySqlConnect;
import util.database.installer.RunTasks;

/**
 * Alternative incase GUI doesn't exist.
 * @author Sahar
 */
public final class DBInstallerConsole implements DBInstallerInterface
{
	private Connection _con;
	
	public DBInstallerConsole()
	{
		final Preferences prop = Preferences.userRoot();
		RunTasks rt = null;
		
		System.out.println("Welcome to GameOne database installer.");
		try (final Scanner scn = new Scanner(System.in))
		{
			while (_con == null)
			{
				System.out.printf("%s (%s): ", "Host", prop.get("dbHost_gameOne", "localhost"));
				String dbHost = scn.nextLine();
				System.out.printf("%s (%s): ", "Port", prop.get("dbPort_gameOne", "3306"));
				String dbPort = scn.nextLine();
				System.out.printf("%s (%s): ", "Username", prop.get("dbUser_gameOne", "root"));
				String dbUser = scn.nextLine();
				System.out.printf("%s (%s): ", "Password", "");
				String dbPass = scn.nextLine();
				System.out.printf("%s (%s): ", "Database", prop.get("dbName_gameOne", "gameOne"));
				String dbDbse = scn.nextLine();
				
				dbHost = dbHost.isEmpty() ? prop.get("dbHost_gameOne", "localhost") : dbHost;
				dbPort = dbPort.isEmpty() ? prop.get("dbPort_gameOne", "3306") : dbPort;
				dbUser = dbUser.isEmpty() ? prop.get("dbUser_gameOne", "root") : dbUser;
				dbDbse = dbDbse.isEmpty() ? prop.get("dbName_gameOne", "gameOne") : dbDbse;
				
				_con = new MySqlConnect(dbHost, dbPort, dbUser, dbPass, dbDbse, true).getConnection();
			}
			
			System.out.print("(C)lean install, (U)pdate or (E)xit? ");
			
			final String resp = scn.next();
			if (resp.equalsIgnoreCase("c"))
			{
				System.out.print("Do you really want to destroy your db (Y/N)?");
				
				if (scn.next().equalsIgnoreCase("y"))
					rt = new RunTasks(this, true);
			}
			else if (resp.equalsIgnoreCase("u"))
				rt = new RunTasks(this, false);
		}
		
		if (rt != null)
			rt.run();
		else
			System.exit(0);
	}
	
	@Override
	public Connection getConnection()
	{
		return _con;
	}
	
	@Override
	public void appendToProgressArea(final String text)
	{
		System.out.println(text);
	}
	
	@Override
	public void showMessage(final String title, final String message, final int type)
	{
		System.out.println(message);
	}
	
	@Override
	public int requestConfirm(final String title, final String message, final int type)
	{
		System.out.print(message);
		
		String res = "";
		try (final Scanner scn = new Scanner(System.in))
		{
			res = scn.next();
		}
		
		return res.equalsIgnoreCase("y") ? 0 : 1;
	}
}