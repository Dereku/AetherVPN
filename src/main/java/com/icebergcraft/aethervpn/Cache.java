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
	String cacheFile = "plugins/AetherVPN/cache.json";
	File cache = new File(cacheFile);
	
	public void CheckCache()
	{
		if (!cache.exists())
		{
			CreateCache();
		}
	}
	
	public boolean isCached(String ip)
	{
		CheckCache();
		
		try
		{
			JsonReader reader = new JsonReader(new FileReader(cacheFile));
			
			ListIpInfo jsonString = new Gson().fromJson(reader, ListIpInfo.class);
			
			for (IpInfo ipInfo : jsonString.getIpList())
			{
				if (ipInfo.ipAddress.equals(ip))
				{
					int days = Integer.parseInt(Main.instance.config.get("cacheTimeDays"));
					
					// Cache expired
					if (Main.instance.config.get("expireCache").equals("true") && ipInfo.instant.toDateTime().plusDays(days).isBefore(DateTime.now()))
					{
						removeFromCache(ipInfo);
						return false;
					}
					return true;
				}
			}			
		}
		catch (Exception e)
		{
			Logging.LogError("Error checking if IP is cached");
			e.printStackTrace();
		}
		return false;
	}
	
	public IpInfo getCachedIpInfo(String ip)
	{
		IpInfo ipInfo = new IpInfo();
		
		JsonReader reader;
		try
		{
			reader = new JsonReader(new FileReader(cacheFile));
			ListIpInfo jsonString = new Gson().fromJson(reader, ListIpInfo.class);
			
			//ipInfo = jsonString.getIpList().stream().findAny().filter(CachedIpInfo -> CachedIpInfo.ipAddress.equals(ip)).get();
			
			for (IpInfo cachedIpInfo : jsonString.getIpList())
			{
				if (cachedIpInfo.ipAddress.equals(ip))
				{
					ipInfo = cachedIpInfo;
					break;
				}
			}
		}
		catch (FileNotFoundException e)
		{
			Logging.LogError("Error getting cached IpInfo!");
			e.printStackTrace();
		}
		
		return ipInfo;
	}
	
	public void CreateCache()
	{
		try
		{
			cache.getParentFile().mkdirs();
			cache.createNewFile();
			
			FileOutputStream outputStream = new FileOutputStream(cache);
			OutputStreamWriter osWriter = new OutputStreamWriter(outputStream);
			
			osWriter.write("{ \"IPList\": [] }");
			osWriter.close();
			outputStream.close();
		}
		catch (Exception e)
		{
			Logging.LogError("Error creating cache!");
			e.printStackTrace();
		}

	}
	
	public void ClearCache()
	{
		cache.delete();
		CheckCache();
	}
	
	public void addToCache(IpInfo ipInfo)
	{
		CheckCache();
		
		try
		{
			JsonReader reader = new JsonReader(new FileReader(cacheFile));
			
			ListIpInfo jsonString = new Gson().fromJson(reader, ListIpInfo.class);
			
			jsonString.getIpList().add(ipInfo);
			
			Type listType = new TypeToken<ListIpInfo>(){}.getType();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String newJson = gson.toJson(jsonString, listType);
			
			FileOutputStream outputStream = new FileOutputStream(cacheFile);
			OutputStreamWriter osWriter = new OutputStreamWriter(outputStream);
			
			osWriter.write(newJson);
			osWriter.close();
			outputStream.close();
		}
		catch (Exception e)
		{
			Logging.LogError("Error adding to cache!");
			e.printStackTrace();
		}
	}
	
	public void removeFromCache(IpInfo ipInfo)
	{
		try
		{
			JsonReader reader = new JsonReader(new FileReader(cacheFile));
			
			ListIpInfo jsonString = new Gson().fromJson(reader, ListIpInfo.class);
			
			jsonString.getIpList().removeIf(cachedIpInfo -> cachedIpInfo.ipAddress.equals(ipInfo.ipAddress));
			
			Type listType = new TypeToken<ListIpInfo>(){}.getType();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String newJson = gson.toJson(jsonString, listType);
			
			FileOutputStream outputStream = new FileOutputStream(cacheFile);
			OutputStreamWriter osWriter = new OutputStreamWriter(outputStream);
			
			osWriter.write(newJson);
			osWriter.close();
			outputStream.close();
		}
		catch (Exception e)
		{
			Logging.LogError("Error removing from cache!");
			e.printStackTrace();
		}
	}
}
