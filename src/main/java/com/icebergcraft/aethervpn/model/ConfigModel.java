package com.icebergcraft.aethervpn.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class ConfigModel {
    @SerializedName("Enabled")
    private boolean enabled = true;

    @SerializedName("BlockVPNs")
    private boolean blockVPNs = true;

    @SerializedName("ApiKey")
    private String apiKey = "";

    @SerializedName("RemainingRequestsWarning")
    private int remainingRequestsWarning = 25;

    @SerializedName("UseCache")
    private boolean useCache = true;

    @SerializedName("ExpireCache")
    private boolean expireCache = true;

    @SerializedName("CacheTimeDays")
    private int cacheTimeDays = 40;

    @SerializedName("LogJoins")
    private boolean logJoins = true;

    @SerializedName("AlertOnlineStaff")
    private boolean alertOnlineStaff = true;

    @SerializedName("WhitelistedIps")
    private String whitelistedIps = "127.0.0.1,192.168.1.1";

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean newEnabled) {
        enabled = newEnabled;
    }

    public boolean getBlockVPNs() {
        return blockVPNs;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getRemainingRequestsWarning() {
        return remainingRequestsWarning;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public boolean isExpireCache() {
        return expireCache;
    }

    public int getCacheTimeDays() {
        return cacheTimeDays;
    }

    public boolean isLogJoins() {
        return logJoins;
    }

    public boolean isAlertOnlineStaff() {
        return alertOnlineStaff;
    }

    public List<String> getWhitelistedIps() {
        return Arrays.asList(whitelistedIps.split(","));
    }
}
