package com.bevelio.ultragames.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import com.bevelio.ultragames.commands.JoinCommand;
import com.bevelio.ultragames.commons.command.CommandManager;
import com.bevelio.ultragames.commons.damage.DamageManager;
import com.bevelio.ultragames.commons.enchantments.EnchantmentManager;
import com.bevelio.ultragames.commons.updater.Updater;
import com.bevelio.ultragames.config.ConfigManager;
import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.listeners.AutoJoinListener;
import com.bevelio.ultragames.listeners.BlockListener;
import com.bevelio.ultragames.listeners.CreatureListener;
import com.bevelio.ultragames.listeners.DamageListener;

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
		
		EnchantmentManager.isNatural(Enchantment.ARROW_DAMAGE);
		
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
