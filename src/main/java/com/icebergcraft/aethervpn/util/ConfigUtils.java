package com.icebergcraft.aethervpn.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.icebergcraft.aethervpn.Main;
import com.icebergcraft.aethervpn.model.ConfigModel;

import java.io.*;

public class ConfigUtils {
    public static ConfigModel CONFIG;
    public final String CONFIG_FILE_LOC = "plugins/AetherVPN/config.json";
    private final File CONFIG_FILE = new File(CONFIG_FILE_LOC);

    public void setupConfig() {
        checkConfig();
        load();
    }

    public void checkConfig() {
        if (!CONFIG_FILE.exists()) {
            createConfig();
        }
    }

    public void createConfig() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            CONFIG_FILE.createNewFile();
            CONFIG = new ConfigModel();
            CONFIG.getDefaultConfig();
            save();
        } catch (Exception ex) {
            Logging.LogError("Error creating cache!");
            ex.printStackTrace();
        }
    }

    public void load() {
        try {
            JsonReader reader = new JsonReader(new FileReader(CONFIG_FILE_LOC));
            CONFIG = new Gson().fromJson(reader, ConfigModel.class);
        } catch (FileNotFoundException ex) {
            Logging.LogError("Failed to load config!");
            Logging.LogError(ex);
        }
    }

    public void save() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String newJson = gson.toJson(CONFIG, ConfigModel.class);

            FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE_LOC);
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
        Main.INSTANCE.getServer().getScheduler().scheduleAsyncDelayedTask(Main.INSTANCE, this::save, 0L);
    }
}
