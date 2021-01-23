package com.icebergcraft.aethervpn.listener;

import com.icebergcraft.aethervpn.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class AVPlayerListener extends PlayerListener {
    private final Main plugin;

    public AVPlayerListener(Main main) {
        this.plugin = main;
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getConfig().getEnabled()) {
            this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, () -> this.plugin.getUtils().checkPlayer(player), 0L);
        }
    }
}
