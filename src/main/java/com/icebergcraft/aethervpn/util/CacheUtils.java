package com.icebergcraft.aethervpn.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icebergcraft.aethervpn.Main;
import com.icebergcraft.aethervpn.model.CacheModel;
import com.icebergcraft.aethervpn.model.IpInfo;
import org.joda.time.DateTime;

import java.io.*;
import java.util.Optional;

public class CacheUtils {
    private final File cacheFile;
    private final Main plugin;
    private CacheModel cacheModel;

    public CacheUtils(Main main) {
        this.plugin = main;
        this.cacheFile = new File(this.plugin.getDataFolder(), "cache.json");
    }

    public CacheModel getCacheModel() {
        return cacheModel;
    }

    public void setupCache() {
        if (!cacheFile.exists()) {
            createCache();
        }
        load();
    }

    public boolean isCached(String ip) {
        Optional<IpInfo> ipInfo = getCacheModel().getIpList().stream().filter(i -> i.ipAddress.equals(ip)).findFirst();

        if (ipInfo.isPresent()) {
            int days = this.plugin.getConfig().getCacheTimeDays();

            // Cache expired
            if (this.plugin.getConfig().isExpireCache() && ipInfo.get().instant.toDateTime().plusDays(days).isBefore(DateTime.now())) {
                removeFromCache(ipInfo.get());
                return false;
            }
            return true;
        }

        return false;
    }

    public Optional<IpInfo> getCachedIpInfo(String ip) {
        Optional<IpInfo> ipInfo = getCacheModel().getIpList().stream().filter(i -> i.ipAddress.equals(ip)).findFirst();

        if (ipInfo.isPresent()) {
            int days = this.plugin.getConfig().getCacheTimeDays();

            // Cache expired
            if (this.plugin.getConfig().isExpireCache() && ipInfo.get().instant.toDateTime().plusDays(days).isBefore(DateTime.now())) {
                removeFromCache(ipInfo.get());
                return Optional.empty();
            }
            return ipInfo;
        }
        return Optional.empty();
    }

    public void createCache() {
        try {
            cacheFile.getParentFile().mkdirs();
            cacheFile.createNewFile();

            cacheModel = new CacheModel();
            save();
        } catch (Exception ex) {
            Logging.LogError("Error creating cache!");
            ex.printStackTrace();
        }
    }

    public void clearCache() {
        cacheFile.delete();
        setupCache();
    }

    public void addToCache(IpInfo ipInfo) {
        getCacheModel().addIpInfo(ipInfo);
        scheduleSave();
    }

    public void removeFromCache(IpInfo ipInfo) {
        getCacheModel().removeIpInfo(ipInfo);
        scheduleSave();
    }

    public void load() {
        try {
            JsonReader reader = new JsonReader(new FileReader(this.cacheFile));
            cacheModel = new Gson().fromJson(reader, CacheModel.class);
        } catch (FileNotFoundException ex) {
            Logging.LogError(ex);
        }
    }

    public void save() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String newJson = gson.toJson(getCacheModel(), CacheModel.class);

            FileOutputStream outputStream = new FileOutputStream(this.cacheFile);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            writer.write(newJson);
            writer.close();
            outputStream.close();
        } catch (Exception ex) {
            Logging.LogError("Error saving cache!");
            Logging.LogError(ex);
        }
    }

    public void scheduleSave() {
        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, this::save, 0L);
    }
}
