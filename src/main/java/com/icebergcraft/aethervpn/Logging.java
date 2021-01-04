package com.icebergcraft.aethervpn;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
	private static final Logger logger = Logger.getLogger("AetherVPN");
	
	public static void LogInfo(String msg)
	{
		logger.log(Level.INFO, msg);
	}
	
	public static void LogError(String msg)
	{
		logger.log(Level.SEVERE, msg);
	}
	
	public static void LogError(Exception e)
	{
		e.printStackTrace();
	}
	
}
