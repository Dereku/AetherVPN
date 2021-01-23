package com.icebergcraft.aethervpn.util;

import com.icebergcraft.aethervpn.Main;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging extends Logger {

    private final String pluginName;

    public Logging(final Main plugin) {
        super(plugin.getClass().getCanonicalName(), null);
        pluginName = "[" + plugin.getDescription().getName() + "] ";
        setParent(plugin.getServer().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(pluginName + logRecord.getMessage());
        super.log(logRecord);
    }
}
