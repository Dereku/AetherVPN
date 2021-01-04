package com.icebergcraft.aethervpn;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class AVPlayerListener extends PlayerListener
{
	public void onPlayerJoin(PlayerJoinEvent event)
	{		
		Player player = event.getPlayer();
		
		Main.instance.getServer().getScheduler().scheduleAsyncDelayedTask(Main.instance, new Runnable() {

		    public void run() {
		    	Main.instance.utils.checkPlayer(player);
		    }
		}, 0L);
	}
}