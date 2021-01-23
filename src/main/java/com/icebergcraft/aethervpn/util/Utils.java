package com.icebergcraft.aethervpn.util;

import com.google.gson.Gson;
import com.icebergcraft.aethervpn.Main;
import com.icebergcraft.aethervpn.model.ConfigModel;
import com.icebergcraft.aethervpn.model.IpInfo;
import com.icebergcraft.aethervpn.model.VPNBlockerRootObject;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
            this.plugin.getLogger().log(Level.INFO, player.getName() + " has joined with the IP: " + ipInfo.ipAddress + " Org: " + ipInfo.org);
        }

        // Alert online staff members
        if (config.isAlertOnlineStaff()) {
            for (Player staff : this.plugin.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("aethervpn.feature.alert")) {
                    staff.sendMessage(player.getName() + " has joined with the IP: " + ipInfo.ipAddress + " Org: " + ipInfo.org);
                }
            }
        }

        if (ipInfo.isHost &&
                config.getBlockVPNs() &&
                !isWhitelisted(ipInfo.ipAddress) &&
                !canBypass(player)) {
            // Log kicks
            if (config.isLogJoins()) {
                this.plugin.getLogger().log(Level.INFO, player.getName() + " has been kicked for using a VPN! (IP: " + ipInfo.ipAddress + " Org: " + ipInfo.org + ")");
            }

            // Alert online staff members
            if (config.isAlertOnlineStaff()) {
                for (Player staff : this.plugin.getServer().getOnlinePlayers()) {
                    if (staff.hasPermission("aethervpn.feature.alert")) {
                        staff.sendMessage(player.getName() + " has been kicked for using a VPN! (IP: " + ipInfo.ipAddress + " Org: " + ipInfo.org + ")");
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

        if (!key.equals(config.getApiKey())) {
            key = "/" + config.getApiKey();
        }

        String urlString = MessageFormat.format("http://api.vpnblocker.net/v2/json/{0}{1}", ip, key);
        String jsonDownload;
        try {
            URL url = new URL(urlString);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // They banned all java User-Agents? kek.
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
            InputStream inputStream = conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream();
            try (InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {
                jsonDownload = br.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
        // API ded
        catch (IOException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Error with the VPNBlocker.net API!", ex);
            return null;
        }
        VPNBlockerRootObject jsonString = new Gson().fromJson(jsonDownload, VPNBlockerRootObject.class);
        String status = jsonString.getStatus();

        // Success!
        if (status.equals("success")) {
            // only check remaining if there is an api key
            if (key.equals("")) {
                if (jsonString.getRemainingRequests() <= (config.getRemainingRequestsWarning())) {
                    this.plugin.getLogger().log(Level.INFO, "You have " + jsonString.getRemainingRequests() + " VPNBlocker.net requests left!");
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
                this.plugin.getLogger().log(Level.WARNING, "You have no more VPNBlocket.net requests left! This plugin will only used cached IPs!");
            }
        }
        return null;
    }

    // Get the actual IP of a player
    public String getPlayerIp(Player player) {
        return (player.getAddress().getAddress().toString()).replaceAll("/", "");
    }

    // Check if user has permission to bypass
    public boolean canBypass(Player player) {
        return player.hasPermission("aethervpn.feature.bypass") || player.isOp();
    }

    // Check if an IP is whitelisted in the config
    public boolean isWhitelisted(String ip) {
        List<String> whitelistedIps = this.plugin.getConfig().getWhitelistedIps();
        Optional<String> foundIp = whitelistedIps.stream().filter(i -> i.equals(ip)).findFirst();

        return foundIp.isPresent();
    }
}
