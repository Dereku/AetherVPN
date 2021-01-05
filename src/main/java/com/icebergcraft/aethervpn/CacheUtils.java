package com.icebergcraft.aethervpn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.Optional;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class CacheUtils
{
	public final String CACHE_FILE_LOC = "plugins/AetherVPN/cache.json";
	private final File CACHE_FILE = new File(CACHE_FILE_LOC);
	public static CacheModel CACHE;

	public void setupCache()
	{
		checkCache();

		// setup the variable
		load();
	}

	public void checkCache()
	{
		if (!CACHE_FILE.exists())
		{
			createCache();
		}
	}
	
	public boolean isCached(String ip)
	{
		Optional<IpInfo> ipInfo = CACHE.getIpList().stream().filter(i -> i.ipAddress.equals(ip)).findFirst();

		if (ipInfo.isPresent())
		{
			int days = Integer.parseInt(Main.INSTANCE.CONFIG.get("cacheTimeDays"));

			// Cache expired
			if (Main.INSTANCE.CONFIG.get("expireCache").equals("true") && ipInfo.get().instant.toDateTime().plusDays(days).isBefore(DateTime.now()))
			{
				removeFromCache(ipInfo.get());
				return false;
			}
			return true;
		}

		return false;
	}
	
	public Optional<IpInfo> getCachedIpInfo(String ip)
	{
		Optional<IpInfo> ipInfo = CACHE.getIpList().stream().filter(i -> i.ipAddress.equals(ip)).findFirst();

		if (ipInfo.isPresent())
		{
			int days = Integer.parseInt(Main.INSTANCE.CONFIG.get("cacheTimeDays"));

			// Cache expired
			if (Main.INSTANCE.CONFIG.get("expireCache").equals("true") && ipInfo.get().instant.toDateTime().plusDays(days).isBefore(DateTime.now()))
			{
				removeFromCache(ipInfo.get());
				return Optional.empty();
			}
			return ipInfo;
		}
		return Optional.empty();
	}
	
	public void createCache()
	{
		try
		{
			CACHE_FILE.getParentFile().mkdirs();
			CACHE_FILE.createNewFile();
			
			CACHE = new CacheModel();
			save();
		}
		catch (Exception ex)
		{
			Logging.LogError("Error creating cache!");
			ex.printStackTrace();
		}
	}
	
	public void clearCache()
	{
		CACHE_FILE.delete();
		setupCache();
	}
	
	public void addToCache(IpInfo ipInfo)
	{
		CACHE.addIpInfo(ipInfo);
		scheduleSave();
	}
	
	public void removeFromCache(IpInfo ipInfo)
	{
		CACHE.removeIpInfo(ipInfo);
		scheduleSave();
	}

	public void load()
	{
		try
		{
			JsonReader reader = new JsonReader(new FileReader(CACHE_FILE_LOC));
			CACHE = new Gson().fromJson(reader, CacheModel.class);
		}
		catch (FileNotFoundException ex)
		{
			Logging.LogError(ex);
		}
	}

	public void save()
	{
		try
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String newJson = gson.toJson(CACHE, CacheModel.class);

			FileOutputStream outputStream = new FileOutputStream(CACHE_FILE_LOC);
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);

			writer.write(newJson);
			writer.close();
			outputStream.close();
		}
		catch (Exception ex)
		{
			Logging.LogError("Error saving cache!");
			Logging.LogError(ex);
		}
	}

	public void scheduleSave()
	{
		Main.INSTANCE.getServer().getScheduler().scheduleAsyncDelayedTask(Main.INSTANCE, this::save, 0L);
	}
}
