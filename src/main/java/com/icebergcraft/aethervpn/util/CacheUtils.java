package com.icebergcraft.aethervpn.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icebergcraft.aethervpn.Main;
import com.icebergcraft.aethervpn.model.CacheModel;
import com.icebergcraft.aethervpn.model.IpInfo;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.logging.Level;

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
            if (this.plugin.getConfig().isExpireCache() && LocalDateTime.ofInstant(ipInfo.get().instant, ZoneOffset.UTC).plusDays(days).isBefore(LocalDateTime.now())) {
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
            if (this.plugin.getConfig().isExpireCache() && LocalDateTime.ofInstant(ipInfo.get().instant, ZoneOffset.UTC).plusDays(days).isBefore(LocalDateTime.now())) {
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
            this.plugin.getLogger().log(Level.WARNING, "Error creating cache!", ex);
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
        try (FileReader fr = new FileReader(this.cacheFile)) {
            JsonReader reader = new JsonReader(fr);
            cacheModel = new Gson().fromJson(reader, CacheModel.class);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Error loading cache", ex);
        }
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String newJson = gson.toJson(getCacheModel(), CacheModel.class);
        try (FileOutputStream outputStream = new FileOutputStream(this.cacheFile);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            writer.write(newJson);
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.WARNING, "Error saving cache!", ex);
        }
    }

    public void scheduleSave() {
        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, this::save, 0L);
    }
}
