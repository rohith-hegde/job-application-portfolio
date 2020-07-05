package config;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs 
{
	public static final boolean writeToFile = true;
	//public static String logFilePath = "C:\\Unturnedhost\\cpanel.log";
	public static final int logLevel = 5; //6 = trace, 0 = none
	
	private static String formatString(String msg, String level)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		String part_time = sdf.format(System.currentTimeMillis());
		String part_level = level;
		
		String res = part_time + " " + part_level + " | " + msg;
		return res;
	}

	public static String getFormattedDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return sdf.format(System.currentTimeMillis());
	}
	
	private static void writeToFile(String str)
	{
		try
		{
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Config.logs_logFilePath, true)), true);
			out.println(str);
			out.close();
		}
		catch (Exception e)
		{
			System.out.println("Logging failed: " + str);
		}
	}
	
	public static void printException(Exception e)
	{
		String stackStr = "";
		try 
		{ 
			e.printStackTrace();
			PrintStream p = new PrintStream(stackStr); 
			e.printStackTrace(p);
			p.flush();
			writeToFile(stackStr);
		} catch (Exception e1) {}
	}
	
	public static void fatal (String str) //an error which is critical enough to cause the application to terminate, for all users
	{
		if (logLevel < 1)
			return;
		str = formatString(str, "FATAL");
		System.out.println(str);
		if (writeToFile)
			writeToFile(str);
	}
	
	public static void error (String str) //an error which is critical enough to cause the application to terminate, for all users
	{
		if (logLevel < 2)
			return;
		str = formatString(str, "ERROR");
		System.out.println(str);
		if (writeToFile)
			writeToFile(str);
	}
	
	public static void warning (String str) //an error which is critical enough to cause the application to terminate, for all users
	{
		if (logLevel < 3)
			return;
		str = formatString(str, "Warn");
		System.out.println(str);
		if (writeToFile)
			writeToFile(str);
	}
	
	public static void info (String str) //an error which is critical enough to cause the application to terminate, for all users
	{
		if (logLevel < 4)
			return;
		str = formatString(str, "Info");
		System.out.println(str);
		if (writeToFile)
			writeToFile(str);
	}
	
	public static void debug (String str) //an error which is critical enough to cause the application to terminate, for all users
	{
		if (logLevel < 5)
			return;
		str = formatString(str, "Debug");
		System.out.println(str);
		if (writeToFile)
			writeToFile(str);
	}
	
	public static void trace (String str) //an error which is critical enough to cause the application to terminate, for all users
	{
		if (logLevel < 6)
			return;
		str = formatString(str, "Trace");
		System.out.println(str);
		if (writeToFile)
			writeToFile(str);
	}
}
