package com.bevelio.ultragames.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
	
	private static String[] downloadWorlds = {"https://dl.dropboxusercontent.com/s/u6udcrc9fhbk41p/DemoMap.zip?dl=0"};
	
	public void loadMaps()
	{
		File maps = new File("maps");
		if(maps.exists())
		{
			return;
		}
		
		maps.mkdir();
		new File("matches").mkdir();
		

		try
		{
			for(String fileLoc : downloadWorlds)
			{
				String url="fileLoc";
			    URL download=new URL(url);
			    String filename="maps/" + download.getFile();
			    ReadableByteChannel rbc=Channels.newChannel(download.openStream());
			    FileOutputStream fileOut = new FileOutputStream(filename);
			    fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
			    fileOut.flush();
			    fileOut.close();
			    rbc.close();
			}
		    
		    System.out.println("SERVER IS SET UP!!!");
		    System.out.println("PLEASE RESTART THE SERVER!");
		    Bukkit.shutdown();
		}
		catch(Exception e)
		{ 
			e.printStackTrace();
		}
	}
	
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
		
		loadMaps();
		
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
