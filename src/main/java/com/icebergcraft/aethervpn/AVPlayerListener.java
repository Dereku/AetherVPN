package com.icebergcraft.aethervpn;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class AVPlayerListener extends PlayerListener
{
	public void onPlayerJoin(PlayerJoinEvent event)
	{		
		Player player = event.getPlayer();
		
		Main.INSTANCE.getServer().getScheduler().scheduleAsyncDelayedTask(Main.INSTANCE, () -> Main.INSTANCE.UTILS.checkPlayer(player), 0L);
	}
}
