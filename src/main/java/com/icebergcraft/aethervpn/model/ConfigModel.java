package com.icebergcraft.aethervpn.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class ConfigModel
{
    @SerializedName("Enabled")
    private boolean enabled;

    @SerializedName("BlockVPNs")
    private boolean blockVPNs;

    @SerializedName("ApiKey")
    private String apiKey;

    @SerializedName("RemainingRequestsWarning")
    private int remainingRequestsWarning;

    @SerializedName("UseCache")
    private boolean useCache;

    @SerializedName("ExpireCache")
    private boolean expireCache;

    @SerializedName("CacheTimeDays")
    private int cacheTimeDays;

    @SerializedName("LogJoins")
    private boolean logJoins;

    @SerializedName("AlertOnlineStaff")
    private boolean alertOnlineStaff;

    @SerializedName("WhitelistedIps")
    private String whitelistedIps;

    public boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean newEnabled)
    {
        enabled = newEnabled;
    }

    public boolean getBlockVPNs()
    {
        return blockVPNs;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public int getRemainingRequestsWarning()
    {
        return remainingRequestsWarning;
    }

    public boolean isUseCache()
    {
        return useCache;
    }

    public boolean isExpireCache()
    {
        return expireCache;
    }

    public int getCacheTimeDays()
    {
        return cacheTimeDays;
    }

    public boolean isLogJoins()
    {
        return logJoins;
    }

    public boolean isAlertOnlineStaff()
    {
        return alertOnlineStaff;
    }

    public List<String> getWhitelistedIps()
    {
        return Arrays.asList(whitelistedIps.split(","));
    }

    public ConfigModel getDefaultConfig()
    {
        enabled = true;
        blockVPNs = true;
        apiKey = "";
        remainingRequestsWarning = 25;
        useCache = true;
        expireCache = true;
        cacheTimeDays = 40;
        logJoins = true;
        alertOnlineStaff = true;
        whitelistedIps = "127.0.0.1,192.168.1.1";

        return this;
    }

}
