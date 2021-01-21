package com.icebergcraft.aethervpn;

import com.icebergcraft.aethervpn.listener.AVPlayerListener;
import com.icebergcraft.aethervpn.model.ConfigModel;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;

public class Main extends JavaPlugin {
    private Utils utils;
    private CacheUtils cacheUtils;
    private ConfigUtils configUtils;

    public void onEnable() {
        utils = new Utils(this);
        cacheUtils = new CacheUtils(this);
        configUtils = new ConfigUtils(this);

        configUtils.setupConfig();
        getCacheUtils().setupCache();

        final AVPlayerListener playerListener = new AVPlayerListener(this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        Logging.LogInfo(MessageFormat.format(
                "Loaded {0} by Johnanater, version: {1}", getDescription().getName(), getDescription().getVersion()
        ));
    }

    public void onDisable() {
        Logging.LogInfo(MessageFormat.format(
                "Unloaded {0} by Johnanater, version: {1}", getDescription().getName(), getDescription().getVersion()
        ));
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

            this.getServer().getScheduler().scheduleAsyncDelayedTask(this, () -> {
                IpInfo ipInfo = getUtils().getIpInfo(getUtils().getPlayerIp(target));

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

            this.getServer().getScheduler().scheduleAsyncDelayedTask(this, () -> {
                IpInfo ipInfo = getUtils().getIpInfo(args[0]);

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
                sender.sendMessage("AetherVPN by Johnanater, version " + getDescription().getVersion());
                return false;
            }

            // Enable the plugin
            if (args[0].equalsIgnoreCase("enable")) {
                getConfig().setEnabled(true);
                configUtils.scheduleSave();
                sender.sendMessage("AetherVPN enabled!");
                return true;
            }

            // Disable the plugin
            if (args[0].equalsIgnoreCase("disable")) {
                getConfig().setEnabled(false);
                configUtils.scheduleSave();
                sender.sendMessage("AetherVPN disabled!");
                return true;
            }

            // Clear cache
            if (args[0].equalsIgnoreCase("clearcache")) {
                getCacheUtils().clearCache();
                configUtils.scheduleSave();
                sender.sendMessage("Cleared IP cache!");
                return true;
            }
        }
        return true;
    }

    public Utils getUtils() {
        return utils;
    }

    public CacheUtils getCacheUtils() {
        return cacheUtils;
    }

    public ConfigModel getConfig() {
        return configUtils.getConfig();
    }
}
