package com.icebergcraft.aethervpn.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icebergcraft.aethervpn.model.ConfigModel;

import java.io.*;
import java.util.Properties;

public class ConfigUtils
{	
	public final String CONFIG_FILE_LOC = "plugins/AetherVPN/config.json";
	private final File CONFIG_FILE = new File(CONFIG_FILE_LOC);
	public static ConfigModel CONFIG;

	public void setupConfig()
	{
		checkConfig();
		load();
	}

	public void checkConfig()
	{
		if(!CONFIG_FILE.exists())
		{
			createConfig();
		}
	}

	public void createConfig()
	{
		try
		{
			CONFIG_FILE.getParentFile().mkdirs();
			CONFIG_FILE.createNewFile();
			CONFIG = new ConfigModel();
			CONFIG.getDefaultConfig();
			save();
		}
		catch (Exception ex)
		{
			Logging.LogError("Error creating cache!");
			ex.printStackTrace();
		}
	}
	
	public void createConfig1()
	{
		Properties prop = new Properties();
		OutputStream output = null;

		try
		{
			File config = new File(CONFIG_FILE_LOC);
			config.getParentFile().mkdirs();
			config.createNewFile();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		try
		{
			output = new FileOutputStream(CONFIG_FILE_LOC);
			
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
		
		catch (IOException ex)
		{	
			ex.printStackTrace();
		}
		
		finally
		{
			if (output != null)
			{
				try
				
				{
					output.close();
				}
				
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}	
		}
	}
	
	public void set(String property, String value)
	{
		Properties prop = new Properties();
		InputStream input = null;
		
		File config = new File(CONFIG_FILE_LOC);
		
		if(!config.exists())
		{
			try
			{
				config.createNewFile();
				checkConfig();
			}
			
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		try 
		{
			input = new FileInputStream(CONFIG_FILE_LOC);
			prop.load(input);

			FileOutputStream output = new FileOutputStream(CONFIG_FILE_LOC);
			
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
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	
	public String get(String property)
	{
		Properties prop = new Properties();
		InputStream input = null;
		
		File config = new File(CONFIG_FILE_LOC);
		
		if(!config.exists())
		{
			try
			{
				config.createNewFile();
				checkConfig();
			}
			
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		try 
		{
			input = new FileInputStream(CONFIG_FILE_LOC);
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
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	public void load()
	{
		try
		{
			JsonReader reader = new JsonReader(new FileReader(CONFIG_FILE_LOC));
			CONFIG = new Gson().fromJson(reader, ConfigModel.class);
		}
		catch (FileNotFoundException ex)
		{
			Logging.LogError("Failed to load config!");
			Logging.LogError(ex);
		}
	}

	public void save()
	{
		try
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String newJson = gson.toJson(CONFIG, ConfigModel.class);

			FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE_LOC);
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);

			writer.write(newJson);
			writer.close();
			outputStream.close();
		}
		catch (Exception ex)
		{
			Logging.LogError("Error saving config!");
			Logging.LogError(ex);
		}
	}
}
