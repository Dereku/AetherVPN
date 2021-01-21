package com.icebergcraft.aethervpn;

import com.icebergcraft.aethervpn.listener.AVPlayerListener;
import com.icebergcraft.aethervpn.model.IpInfo;
import com.icebergcraft.aethervpn.util.CacheUtils;
import com.icebergcraft.aethervpn.util.ConfigUtils;
import com.icebergcraft.aethervpn.util.Logging;
import com.icebergcraft.aethervpn.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;

public class Main extends JavaPlugin {
    public static Main INSTANCE;
    private final AVPlayerListener playerListener = new AVPlayerListener();
    private final String version = "1.1.0";
    public Utils UTILS;
    public CacheUtils CACHE;
    public ConfigUtils CONFIG;

    public void onEnable() {
        INSTANCE = this;
        UTILS = new Utils();
        CACHE = new CacheUtils();
        CONFIG = new ConfigUtils();

        CONFIG.setupConfig();
        CACHE.setupCache();

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        Logging.LogInfo(MessageFormat.format("Loaded {0} by Johnanater, version: {1}", getDescription().getName(), version));
    }

    public void onDisable() {
        Logging.LogInfo(MessageFormat.format("Unloaded {0} by Johnanater, version: {1}", getDescription().getName(), version));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("playerinfo") && sender.hasPermission("aethervpn.command.playerinfo")) {
            if (args.length == 0) {
                return false;
            }

            Player target = getServer().getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage("That player isn't online!");
                return false;
            }

            Main.INSTANCE.getServer().getScheduler().scheduleAsyncDelayedTask(Main.INSTANCE, () ->
            {
                IpInfo ipInfo = UTILS.getIpInfo(UTILS.getPlayerIp(target));

                sender.sendMessage(MessageFormat.format("Info for {0}:", target.getName()));
                sender.sendMessage(MessageFormat.format("IP Address: {0}", ipInfo.ipAddress));
                sender.sendMessage(MessageFormat.format("IP Org: {0} IsHost: {1}", ipInfo.org, ipInfo.isHost));
            }, 0L);

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("lookup") && sender.hasPermission("aethervpn.command.lookup")) {
            if (args.length == 0) {
                return false;
            }

            Main.INSTANCE.getServer().getScheduler().scheduleAsyncDelayedTask(Main.INSTANCE, () ->
            {
                IpInfo ipInfo = UTILS.getIpInfo(args[0]);

                try {
                    sender.sendMessage(MessageFormat.format("Info for {0}:", ipInfo.ipAddress));
                    sender.sendMessage(MessageFormat.format("IP Org: {0} IsHost: {1}", ipInfo.org, ipInfo.isHost));
                } catch (Exception ex) {
                    sender.sendMessage("There has been an error with the API!");
                }
            }, 0L);

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("aethervpn") && sender.hasPermission("aethervpn.command.aethervpn")) {
            // Display version info
            if (args.length == 0) {
                sender.sendMessage(MessageFormat.format("AetherVPN by Johnanater, version {0}", version));
                return false;
            }

            // Enable the plugin
            if (args[0].equalsIgnoreCase("enable")) {
                ConfigUtils.CONFIG.setEnabled(true);
                sender.sendMessage("AetherVPN enabled!");
                return true;
            }

            // Disable the plugin
            if (args[0].equalsIgnoreCase("disable")) {
                ConfigUtils.CONFIG.setEnabled(false);
                CONFIG.scheduleSave();
                sender.sendMessage("AetherVPN disabled!");
                return true;
            }

            // Clear cache
            if (args[0].equalsIgnoreCase("clearcache")) {
                CACHE.clearCache();
                CONFIG.scheduleSave();
                sender.sendMessage("Cleared IP cache!");
                return true;
            }
        }
        return true;
    }
}
