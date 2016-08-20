package com.bevelio.bevelio.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.bevelio.bevelio.commands.JoinCommand;
import com.bevelio.bevelio.commons.command.CommandManager;
import com.bevelio.bevelio.commons.damage.DamageManager;
import com.bevelio.bevelio.commons.updater.Updater;
import com.bevelio.bevelio.config.ConfigManager;
import com.bevelio.bevelio.core.MatchManager;
import com.bevelio.bevelio.listeners.AutoJoinListener;
import com.bevelio.bevelio.listeners.BlockListener;
import com.bevelio.bevelio.listeners.CreatureListener;
import com.bevelio.bevelio.listeners.DamageListener;

public class BevelioPlugin extends JavaPlugin
{
	private static MatchManager matchManager;
	private static ConfigManager configManager;
	private static BevelioPlugin instance;
	
	
	public void registerCommands()
	{
		CommandManager cm = new CommandManager();
		cm.registerCommand(new JoinCommand());
	}
	
	public void registerListener()
	{
		Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
		
		Bukkit.getPluginManager().registerEvents(new AutoJoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getPluginManager().registerEvents(new CreatureListener(), this);
	}
	
	@Override
	public void onEnable()
	{
		new Updater(this);
		new DamageManager(this);
		
		instance = this;
		configManager = new ConfigManager();
		matchManager = new MatchManager();
		Bukkit.getPluginManager().registerEvents(matchManager, this);
		
		this.registerListener();
		this.registerCommands();
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.kickPlayer(ChatColor.RED + "Server is restarting!");
		});
		matchManager.resetServer();
	}
	
	public static MatchManager getMatchManager()
	{
		return matchManager;
	}
	
	public static ConfigManager getConfigManager()
	{
		return configManager;
	}
	
	public static BevelioPlugin getInstance()
	{
		return instance;
	}
}
