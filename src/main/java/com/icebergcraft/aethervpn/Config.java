package com.icebergcraft.aethervpn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Config
{	
	String configFile = "plugins/AetherVPN/config.properties";
	
	public void checkConfig()
	{		
		File config = new File(configFile);
		
		if(!config.exists())
		{
			createConfig();
		}
	}
	
	public void createConfig()
	{
		Properties prop = new Properties();
		OutputStream output = null;
		
		
		try
		{
			File config = new File(configFile);
			config.getParentFile().mkdirs();
			config.createNewFile();
			
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			output = new FileOutputStream(configFile);
			
			prop.setProperty("apiKey", "");
			
			// Stuff
			prop.setProperty("enabled", "true");
			prop.setProperty("useCache", "true");
			prop.setProperty("blockVPNs", "true");
			
			// Whitelisted ips
			prop.setProperty("whitelistedIps", "127.0.01,192.168.1.1");
			
			// Logging
			prop.setProperty("logJoins", "true");
			prop.setProperty("alertOnlineStaff", "true");
			
			// Cache
			prop.setProperty("expireCache", "true");
			prop.setProperty("cacheTimeDays", "40");
			
			// API stuff
			prop.setProperty("remainingRequestsWarning", "25");
			
			prop.store(output, null);
		}
		
		catch (IOException e)
		{	
			e.printStackTrace();
		}
		
		finally
		{
			if (output != null)
			{
				try
				
				{
					output.close();
				}
				
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}	
		}
	}
	
	public void set(String property, String value)
	{
		Properties prop = new Properties();
		InputStream input = null;
		
		File config = new File(configFile);
		
		if(!config.exists())
		{
			try
			{
				config.createNewFile();
				checkConfig();
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try 
		{
			input = new FileInputStream(configFile);
			prop.load(input);

			FileOutputStream output = new FileOutputStream(configFile);
			
			prop.setProperty(property, value);
			prop.store(output, null);
		}
		
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public String get(String property)
	{
		Properties prop = new Properties();
		InputStream input = null;
		
		File config = new File(configFile);
		
		if(!config.exists())
		{
			try
			{
				config.createNewFile();
				checkConfig();
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try 
		{
			input = new FileInputStream(configFile);
			prop.load(input);

			return(prop.getProperty(property));
		}
		
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
