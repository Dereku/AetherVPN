package com.icebergcraft.aethervpn.util;

import com.google.gson.Gson;
import com.icebergcraft.aethervpn.Main;
import com.icebergcraft.aethervpn.model.ConfigModel;
import com.icebergcraft.aethervpn.model.IpInfo;
import com.icebergcraft.aethervpn.model.VPNBlockerRootObject;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class Utils {
    private final Main plugin;

    public Utils(Main main) {
        this.plugin = main;
    }

    public void checkPlayer(Player player) {
        final ConfigModel config = this.plugin.getConfig();
        IpInfo ipInfo = getIpInfo(getPlayerIp(player));

        // Log joins
        if (config.isLogJoins()) {
            this.plugin.getLogger().log(Level.INFO, "{0} has joined with the IP: {1} Org: {2}", new Object[]{player.getName(), ipInfo.ipAddress, ipInfo.org});
        }

        // Alert online staff members
        if (config.isAlertOnlineStaff()) {
            for (Player staff : this.plugin.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("aethervpn.feature.alert")) {
                    staff.sendMessage(MessageFormat.format("{0} has joined with the IP: {1} Org: {2}", player.getName(), ipInfo.ipAddress, ipInfo.org));
                }
            }
        }

        if (ipInfo.isHost &&
                config.getBlockVPNs() &&
                !isWhitelisted(ipInfo.ipAddress) &&
                !canBypass(player)) {
            // Log kicks
            if (config.isLogJoins()) {
                this.plugin.getLogger().log(Level.INFO, "{0} has been kicked for using a VPN! (IP: {1} Org: {2})", new Object[]{player.getDisplayName(), ipInfo.ipAddress, ipInfo.org});
            }

            // Alert online staff members
            if (config.isAlertOnlineStaff()) {
                for (Player staff : this.plugin.getServer().getOnlinePlayers()) {
                    if (staff.hasPermission("aethervpn.feature.alert")) {
                        staff.sendMessage(MessageFormat.format("{0} has been kicked for using a VPN! (IP: {1} Org: {2})", player.getDisplayName(), ipInfo.ipAddress, ipInfo.org));
                    }
                }
            }

            player.kickPlayer("You've been kicked for using a VPN!");
        }
    }

    // Get IpInfo
    public IpInfo getIpInfo(String ip) {
        Optional<IpInfo> ipInfoCached = this.plugin.getCacheUtils().getCachedIpInfo(ip);

        return ipInfoCached.orElseGet(() -> DownloadIpInfo(ip));
    }

    // Download IpInfo from API
    public IpInfo DownloadIpInfo(String ip) {
        final ConfigModel config = this.plugin.getConfig();
        String key = "";

        if (!config.getApiKey().equals("")) {
            key = "/" + config.getApiKey();
        }

        String url = MessageFormat.format("http://api.vpnblocker.net/v2/json/{0}{1}", ip, key);

        try {
            String jsonDownload = Unirest.get(url).asString().getBody();

            VPNBlockerRootObject jsonString = new Gson().fromJson(jsonDownload, VPNBlockerRootObject.class);

            String status = jsonString.getStatus();

            //System.out.println(jsonDownload);

            // Success!
            if (status.equals("success")) {
                // only check remaining if there is an api key
                if (key.equals("")) {
                    if (jsonString.getRemainingRequests() <= (config.getRemainingRequestsWarning())) {
                        this.plugin.getLogger().log(Level.INFO, "You have {0} VPNBlocker.net requests left!", jsonString.getRemainingRequests());
                    }
                }

                IpInfo ipInfo = new IpInfo();

                ipInfo.ipAddress = jsonString.getIpaddress();
                ipInfo.isHost = jsonString.getHostIp();
                ipInfo.org = jsonString.getOrg();
                ipInfo.instant = DateTime.now().toInstant();

                if (config.isUseCache()) {
                    this.plugin.getCacheUtils().addToCache(ipInfo);
                }
                return ipInfo;
            }

            // The query returned failed
            String msg = jsonString.getMsg();

            if (status.equals("failed") && !msg.equals("Invalid IP Address")) {
                this.plugin.getLogger().log(Level.WARNING, "VPNBlocker.net API returned failed! Status Message: " + msg);

                if (msg.equals("Monthly Request Limit Reached")) {
                    this.plugin.getLogger().log(Level.WARNING,"You have no more VPNBlocket.net requests left! This plugin will only used cached IPs!");
                }
            }
        }
        // API ded
        catch (UnirestException ex) {
            this.plugin.getLogger().log(Level.WARNING,"Error with the VPNBlocker.net API!", ex);
        }
        return null;
    }

    // Get the actual IP of a player
    public String getPlayerIp(Player player) {
        String ip = (player.getAddress().getAddress().toString()).replaceAll("/", "");
        return ip;
    }

    // Check if user has permission to bypass
    public boolean canBypass(Player player) {
        if (player.hasPermission("aethervpn.feature.bypass"))
            return true;

        return player.isOp();
    }

    // Check if an IP is whitelisted in the config
    public boolean isWhitelisted(String ip) {
        List<String> whitelistedIps = this.plugin.getConfig().getWhitelistedIps();
        Optional<String> foundIp = whitelistedIps.stream().filter(i -> i.equals(ip)).findFirst();

        return foundIp.isPresent();
    }
}
