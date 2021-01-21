package com.icebergcraft.aethervpn.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icebergcraft.aethervpn.Main;
import com.icebergcraft.aethervpn.model.ConfigModel;

import java.io.*;

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
            config.getDefaultConfig();
            save();
        } catch (Exception ex) {
            Logging.LogError("Error creating cache!");
            ex.printStackTrace();
        }
    }

    public void load() {
        try {
            JsonReader reader = new JsonReader(new FileReader(this.configFile));
            config = new Gson().fromJson(reader, ConfigModel.class);
        } catch (FileNotFoundException ex) {
            Logging.LogError("Failed to load config!");
            Logging.LogError(ex);
        }
    }

    public void save() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String newJson = gson.toJson(getConfig(), ConfigModel.class);

            FileOutputStream outputStream = new FileOutputStream(this.configFile);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            writer.write(newJson);
            writer.close();
            outputStream.close();
        } catch (Exception ex) {
            Logging.LogError("Error saving config!");
            Logging.LogError(ex);
        }
    }

    public void scheduleSave() {
        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, this::save, 0L);
    }
}
