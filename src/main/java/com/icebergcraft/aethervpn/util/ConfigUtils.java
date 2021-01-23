package com.icebergcraft.aethervpn.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icebergcraft.aethervpn.Main;
import com.icebergcraft.aethervpn.model.ConfigModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ConfigUtils {
    private final File configFile;
    private final Main plugin;
    private ConfigModel config;

    public ConfigUtils(Main main) {
        this.plugin = main;
        this.configFile = new File(this.plugin.getDataFolder(), "config.json");
    }

    public ConfigModel getConfig() {
        return config;
    }

    public void setupConfig() {
        if (!configFile.exists()) {
            createConfig();
        }
        load();
    }

    public void createConfig() {
        try {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
            config = new ConfigModel();
            save();
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.WARNING, "Error creating cache!", ex);
        }
    }

    public void load() {
        try (FileReader fr = new FileReader(this.configFile)) {
            JsonReader reader = new JsonReader(fr);
            config = new Gson().fromJson(reader, ConfigModel.class);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to load config!", ex);
        }
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String newJson = gson.toJson(getConfig(), ConfigModel.class);
        try (FileOutputStream outputStream = new FileOutputStream(this.configFile);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            writer.write(newJson);
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.WARNING, "Error saving config!", ex);
        }
    }

    public void scheduleSave() {
        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, this::save, 0L);
    }
}
