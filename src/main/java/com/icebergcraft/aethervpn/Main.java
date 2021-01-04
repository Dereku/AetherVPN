package com.icebergcraft.aethervpn;

import java.text.MessageFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main extends JavaPlugin
{
	public static Main instance;
	public Utils utils;
	public Cache cache;
	public Config config;
	
	public Permissions Permissions;
	
	private final AVPlayerListener playerListener = new AVPlayerListener();
	
	String version = "1.0.0.1";
	
	public void onEnable()
	{
		instance = this;
		utils = new Utils();
		cache = new Cache();
		config = new Config();
		
		instance.config.checkConfig();
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
	}
	
	public void onDisable()
	{
		
	}
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {    	
    	if (cmd.getName().equalsIgnoreCase("playerinfo") && hasPermission((Player)sender, "aethervpn.playerinfo"))
    	{
    		if (args.length == 0)
    		{
    			return false;
    		}
    		
    		Player target = getServer().getPlayer(args[0]);
    		
    		if (target == null)
    			sender.sendMessage("That player isn't online!");
    		
    		Main.instance.getServer().getScheduler().scheduleAsyncDelayedTask(Main.instance, new Runnable() {

    		    public void run() {
    	    		IpInfo ipInfo = utils.getIpInfo(utils.getPlayerIp(target));
    	    		
    	    		sender.sendMessage(MessageFormat.format("Info for {0}:", target.getName()));
    	    		sender.sendMessage(MessageFormat.format("IP Address: {0}", ipInfo.ipAddress));
    	    		sender.sendMessage(MessageFormat.format("IP Org: {0} IsHost: {1}", ipInfo.org, ipInfo.isHost));
    		    }
    		}, 0L);
    		
    		return true;
    	}
    	
    	if (cmd.getName().equalsIgnoreCase("lookup") && hasPermission((Player)sender, "aethervpn.lookup"))
    	{
    		if (args.length == 0)
    		{
    			return false;
    		}
    		
    		Main.instance.getServer().getScheduler().scheduleAsyncDelayedTask(Main.instance, new Runnable() {

    		    public void run() {
    	    		IpInfo ipInfo = utils.getIpInfo(args[0]);
    	    		
    	    		try
    	    		{
    	    			sender.sendMessage(MessageFormat.format("Info for {0}:", ipInfo.ipAddress));
    	    			sender.sendMessage(MessageFormat.format("IP Org: {0} IsHost: {1}", ipInfo.org, ipInfo.isHost));
    	    		}
    	    		catch (Exception e)
    	    		{
    	    			sender.sendMessage("There has been an error with the API!");
    	    		}
    		    }
    		}, 0L);

    		return true;
    	}
    	
    	if (cmd.getName().equalsIgnoreCase("aethervpn") && hasPermission((Player)sender, "aethervpn"))
    	{
    		// Display version info
    		if (args.length == 0)
    		{
    			sender.sendMessage(MessageFormat.format("AetherVPN by Johnanater, version {0}", version));
    			return false;
    		}
    		
    		// Enable the plugin
    		if (args[0].equalsIgnoreCase("enable"))
    		{
    			config.set("enabled", "true");
    			sender.sendMessage("AetherVPN enabled!");
    			return true;
    		}
    		
    		// Disable the plugin
    		if (args[0].equalsIgnoreCase("disable"))
    		{
    			config.set("enabled", "false");
    			sender.sendMessage("AetherVPN disabled!");
    			return true;
    		}
    		
    		// Clear cache
    		if (args[0].equalsIgnoreCase("clearcache"))
    		{
    			cache.ClearCache();
    			sender.sendMessage("Cleared IP cache!");
    			return true;
    		}
    	}
    	return true; 
    }
    
    public boolean hasPermission(Player player, String permission)
    {
    	if (player.isOp())
    		return true;
    	
    	PluginManager pm = getServer().getPluginManager();
    	
    	final Plugin permExPlugin = pm.getPlugin("PermissionsEx");
    	if (permExPlugin != null && permExPlugin.isEnabled())
    	{
    		PermissionManager manager = PermissionsEx.getPermissionManager();
    		return manager.has(player, permission);
    	}
    	
    	final Plugin permPlugin = pm.getPlugin("Permissions");
    	if (permPlugin != null && permPlugin.isEnabled())
    	{
    		return Permissions.getHandler().has(player, permission);
    	}
            
        return false;
    }
    
}
