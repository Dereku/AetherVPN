package com.icebergcraft.aethervpn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class Cache
{
	public final String CACHE_FILE = "plugins/AetherVPN/cache.json";
	private final File CACHE = new File(CACHE_FILE);
	
	public void CheckCache()
	{
		if (!CACHE.exists())
		{
			CreateCache();
		}
	}
	
	public boolean isCached(String ip)
	{
		CheckCache();
		
		try
		{
			JsonReader reader = new JsonReader(new FileReader(CACHE_FILE));
			
			ListIpInfo jsonString = new Gson().fromJson(reader, ListIpInfo.class);
			
			for (IpInfo ipInfo : jsonString.getIpList())
			{
				if (ipInfo.ipAddress.equals(ip))
				{
					int days = Integer.parseInt(Main.INSTANCE.CONFIG.get("cacheTimeDays"));
					
					// Cache expired
					if (Main.INSTANCE.CONFIG.get("expireCache").equals("true") && ipInfo.instant.toDateTime().plusDays(days).isBefore(DateTime.now()))
					{
						removeFromCache(ipInfo);
						return false;
					}
					return true;
				}
			}			
		}
		catch (Exception ex)
		{
			Logging.LogError("Error checking if IP is cached");
			ex.printStackTrace();
		}
		return false;
	}
	
	public IpInfo getCachedIpInfo(String ip)
	{
		IpInfo ipInfo = new IpInfo();
		
		JsonReader reader;
		try
		{
			reader = new JsonReader(new FileReader(CACHE_FILE));
			ListIpInfo jsonString = new Gson().fromJson(reader, ListIpInfo.class);
			
			for (IpInfo cachedIpInfo : jsonString.getIpList())
			{
				if (cachedIpInfo.ipAddress.equals(ip))
				{
					ipInfo = cachedIpInfo;
					break;
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			Logging.LogError("Error getting cached IpInfo!");
			ex.printStackTrace();
		}
		
		return ipInfo;
	}
	
	public void CreateCache()
	{
		try
		{
			CACHE.getParentFile().mkdirs();
			CACHE.createNewFile();
			
			FileOutputStream outputStream = new FileOutputStream(CACHE);
			OutputStreamWriter osWriter = new OutputStreamWriter(outputStream);
			
			osWriter.write("{ \"IPList\": [] }");
			osWriter.close();
			outputStream.close();
		}
		catch (Exception ex)
		{
			Logging.LogError("Error creating cache!");
			ex.printStackTrace();
		}

	}
	
	public void ClearCache()
	{
		CACHE.delete();
		CheckCache();
	}
	
	public void addToCache(IpInfo ipInfo)
	{
		CheckCache();
		
		try
		{
			JsonReader reader = new JsonReader(new FileReader(CACHE_FILE));
			
			ListIpInfo jsonString = new Gson().fromJson(reader, ListIpInfo.class);
			
			jsonString.getIpList().add(ipInfo);
			
			Type listType = new TypeToken<ListIpInfo>(){}.getType();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String newJson = gson.toJson(jsonString, listType);
			
			FileOutputStream outputStream = new FileOutputStream(CACHE_FILE);
			OutputStreamWriter osWriter = new OutputStreamWriter(outputStream);
			
			osWriter.write(newJson);
			osWriter.close();
			outputStream.close();
		}
		catch (Exception ex)
		{
			Logging.LogError("Error adding to cache!");
			ex.printStackTrace();
		}
	}
	
	public void removeFromCache(IpInfo ipInfo)
	{
		try
		{
			JsonReader reader = new JsonReader(new FileReader(CACHE_FILE));
			
			ListIpInfo jsonString = new Gson().fromJson(reader, ListIpInfo.class);
			
			jsonString.getIpList().removeIf(cachedIpInfo -> cachedIpInfo.ipAddress.equals(ipInfo.ipAddress));
			
			Type listType = new TypeToken<ListIpInfo>(){}.getType();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String newJson = gson.toJson(jsonString, listType);
			
			FileOutputStream outputStream = new FileOutputStream(CACHE_FILE);
			OutputStreamWriter osWriter = new OutputStreamWriter(outputStream);
			
			osWriter.write(newJson);
			osWriter.close();
			outputStream.close();
		}
		catch (Exception ex)
		{
			Logging.LogError("Error removing from cache!");
			ex.printStackTrace();
		}
	}
}
