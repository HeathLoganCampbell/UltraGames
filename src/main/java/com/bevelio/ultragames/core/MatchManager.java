package com.bevelio.ultragames.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bevelio.ultragames.commons.Settings;
import com.bevelio.ultragames.commons.updater.UpdateEvent;
import com.bevelio.ultragames.commons.updater.UpdateType;
import com.bevelio.ultragames.commons.utils.PlayerUtils;
import com.bevelio.ultragames.events.MatchStateChangeEvent;
import com.bevelio.ultragames.games.ctw.CTW;
import com.bevelio.ultragames.games.dtc.DTC;
import com.bevelio.ultragames.games.dtm.DTM;
import com.bevelio.ultragames.games.tdm.TDM;
import com.bevelio.ultragames.kit.Kit;
import com.bevelio.ultragames.map.WorldData;
import com.bevelio.ultragames.map.WorldManager;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class MatchManager implements Listener
{
	private MatchState state;
	private int seconds
			  , id
			  , loadErrors;
	private Match match, nextMatch;
	
	private WorldManager worldManager;
	
	private HashSet<UUID> autoJoin
						, spectators;
	private HashMap<String, Class<? extends Match>> matchtypes;
	
	public MatchManager()
	{
		this.state = MatchState.LOADING;
		this.seconds = 0;
		this.id = 0;
		this.loadErrors = 0;
		
		this.worldManager = new WorldManager();
		
		
		this.autoJoin = new HashSet<>();
		this.spectators = new HashSet<>();
		this.matchtypes = new HashMap<>();
		
		this.addMatchType("TDM", TDM.class);
		this.addMatchType("DTC", DTC.class);
		this.addMatchType("CTW", CTW.class);
		this.addMatchType("DTM", DTM.class);
	}
	
	public MatchState getState()
	{
		return this.state;
	}
	
	public void setState(MatchState matchState)
	{
		Bukkit.broadcastMessage(ChatColor.GRAY + "MatchState]: " + this.state.name() + " => " + matchState.name());
		MatchStateChangeEvent event = new MatchStateChangeEvent(matchState, this.state);
		Bukkit.getPluginManager().callEvent(event);
		
		if(event.isCancelled())
		{
			return;
		}
		
		this.state = matchState;
		if(this.state.isTimable())
		{
			this.setSeconds(this.state.getSeconds());
		}
	}
	
	public void nextGameState()
	{
		int currentStateId = this.getState().ordinal();
		if(currentStateId < MatchState.values().length)
		{
			MatchState nextState = MatchState.values()[currentStateId + 1];
			this.setState(nextState);
		}
	}
	
	public void setSeconds(int seconds)
	{
		this.seconds = seconds;
	}
	
	public int getSeconds()
	{
		return this.seconds;
	}
	
	public void addMatchType(String name, Class<? extends Match> match)
	{
		this.matchtypes.put(name, match);
	}
	
	public Class<? extends Match> getMatchType(String name)
	{
		return this.matchtypes.get(name);
	}
	
	public void resetServer()
	{
		for(int i = 0; i <= this.id; i++)
		{
			this.worldManager.deleteMatch(i);
		}
	}
	
	public boolean isPlaying(Player player)
	{
		return !this.spectators.contains(player.getUniqueId());
//		for(Team team : this.match.getAllTeam())
//		{
//			if(team.isMember(player.getUniqueId()))
//			{
//				return true;
//			}
//		}
//		return false;
	}
	
	public Match getMatch()
	{
		return this.match;
	}
	
	private void setSpectatorInventory(Player player)
	{
		PlayerUtils.reset(player, false);
		Inventory inv = player.getInventory();
		inv.setItem(0, new ItemStack(Material.SLIME_BALL));
	}
	
	public void addSpectator(Player player)
	{
		this.spectators.add(player.getUniqueId());
		player.setGameMode(GameMode.CREATIVE);
		this.setSpectatorInventory(player);
		Bukkit.getOnlinePlayers().forEach(viewer ->
		{
			if(viewer != player && !this.spectators.contains(viewer.getUniqueId()))
			{
				viewer.hidePlayer(player);
			}
		});
	}
	
	public void removeSpectator(Player player)
	{
		this.spectators.remove(player.getUniqueId());
		player.setGameMode(GameMode.SURVIVAL);
		Bukkit.getOnlinePlayers().forEach(viewer ->
		{
			if(viewer != player)
			{
				viewer.showPlayer(player);
				if(this.spectators.contains(viewer.getUniqueId()))
				{
					player.hidePlayer(viewer);
				}
			}
		});
	}
	
	public Team joinMatch(Player player)
	{
		if(this.match == null)
		{
			return null;
		}
		Team team = this.match.selectTeam(null);
		if(team == null)
		{
			return null;
		}
		team.addMember(player.getUniqueId());
		this.match.spawn(player);
		this.removeSpectator(player);
		return team;
	}
	
	public Match createMatch()
	{
		WorldData worldData = this.worldManager.createNewWorld(++this.id);
		Class<? extends Match> matchClazz = this.getMatchType(worldData.gameType);
		if(matchClazz == null)
		{
			for(int i = 0; i < 6; i++)
			{
				System.out.println();
			}
			System.out.println("Failed to find the match type '" + worldData.gameType + "' for world '" + worldData.displayName + "'!");
			if(loadErrors >= 10)
			{
				System.out.println("There has been too many errors on the server!");
				Bukkit.shutdown();
			}
			if(this.worldManager.getMatchWorldsSize() > 2)
			{
				loadErrors++;
				return this.createMatch();
			}
		}
		
		Match match = null;
		
		try
		{
			match = matchClazz.newInstance();
			match.setWorldData(worldData);
			
			
			File kitFile = new File(worldData.world.getName() + "/kits.yml");
			System.out.println(kitFile.toString());
	        ConfigurationSection config = YamlConfiguration.loadConfiguration(kitFile);
	        for(Kit kit : BevelioPlugin.getConfigManager().loadKits(config))
	        {
	        	match.addKit(kit);
	        	System.out.println("Kit loaded " + kit.getName());
	        }
	        
	        File mapFile = new File(worldData.world.getName() + "/Map.yml");
			System.out.println(mapFile.toString());
	        config = YamlConfiguration.loadConfiguration(mapFile);
	        
	        BevelioPlugin.getConfigManager().loadMap(config, worldData);
	        
		} 
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return match;
	}
	
	
	@EventHandler
	public void onPing(ServerListPingEvent e)
	{
		String MOTD = ChatColor.BOLD + "       Bevelio.com\n";
		if(this.getState() == MatchState.LIVE)
		{
			MOTD += ChatColor.DARK_AQUA + "Now Playing ";
		}
		e.setMotd(MOTD);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		e.setJoinMessage(null);
		World world = Bukkit.getWorld(Settings.GAME_WORLD_NAME + this.id);
		if(world == null)
		{
			player.kickPlayer(ChatColor.RED + "Server is restarting\n\n" + ChatColor.GRAY + "Please reconnect in 10 seconds");
		}
		player.teleport(world.getSpawnLocation());
		this.addSpectator(player);
		player.sendMessage(ChatColor.BOLD + "You are in spectator mode do /join to join the game!");
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		e.setQuitMessage(null);
		this.removeSpectator(player);
		for(Team team : this.match.getAllTeam())
		{
			if(team.isMember(player.getUniqueId()))
			{
				team.removeMember(player.getUniqueId());
			}
		}
	}
	
	@EventHandler
	public void onStateChange(MatchStateChangeEvent e)
	{
		if(e.getTo() == MatchState.FINISHING || e.getFrom() == MatchState.LOADING)
		{
			this.nextMatch = createMatch();
		}
				
		if(e.getTo() == MatchState.WAITING)
		{
			this.match = this.nextMatch;
			World world = Bukkit.getWorld(Settings.GAME_WORLD_NAME + this.id);
			Bukkit.getOnlinePlayers().forEach(player -> player.teleport(world.getSpawnLocation()));
			this.worldManager.deleteMatch(this.id - 1);
		}
		
		if(e.getTo() == MatchState.FINISHING && e.getFrom() == MatchState.LIVE)
		{
			HandlerList.unregisterAll(this.match);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				for(Player viewer : Bukkit.getOnlinePlayers())
				{
					viewer.showPlayer(player);
				}
				player.setGameMode(GameMode.CREATIVE);
				this.spectators.add(player.getUniqueId());
				this.setSpectatorInventory(player);
			}
		}
		
		if(e.getTo() == MatchState.LIVE)
		{
			Bukkit.getPluginManager().registerEvents(this.match, BevelioPlugin.getInstance());
		}
	}
	
	@EventHandler
	public void onUpdateSecond(UpdateEvent e)
	{
		if(e.getType() != UpdateType.SECOND)
		{
			return;
		}
		
		switch(this.state)
		{
			case LOADING:
				this.setState(MatchState.WAITING);
				break;
			case WAITING:
				this.setState(MatchState.STARTING);
				break;
			case STARTING:
				if(this.seconds < 5 || this.seconds % 5 == 0)
					Bukkit.broadcastMessage(ChatColor.GREEN + "Game ending in " + ChatColor.RED + this.getSeconds() + ChatColor.GREEN + " second" + (this.getSeconds() == 1 ? "" : "s") + "!");
				if(this.getSeconds() <= 1)
				{
					this.match.start();
				}
				break;
			case LIVE:
				if(this.getSeconds() <= 1)
				{
					
				}
				break;
			case FINISHING:
				if(this.seconds < 5 || this.seconds % 5 == 0)
					Bukkit.broadcastMessage(ChatColor.GREEN + "Game ending in " + ChatColor.RED + this.getSeconds() + ChatColor.GREEN + " second" + (this.getSeconds() == 1 ? "" : "s") + "!");
				if(this.getSeconds() <= 1)
				{
					this.setState(MatchState.WAITING);
				}
				break;
			case ENDED:
				break;
			default:
				break;
		}
		
		if(this.getState().isTimable())
		{
			this.setSeconds(this.getSeconds() - 1);
			if(this.getSeconds() <= 0)
			{
				this.nextGameState();
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		Location deathLoc = player.getLocation();
		if(this.getMatch() == null)
		{
			return;
		}
		
		if(!this.isPlaying(player))
		{
			return;
		}
		
		Team team = this.getMatch().getTeam(player);
		if(team == null)
		{
			player.teleport(player.getWorld().getSpawnLocation());
			return;
		}
	
		for(ItemStack item : (ItemStack[]) ArrayUtils.addAll(player.getInventory().getContents(), player.getInventory().getArmorContents()))
		{
			if(item != null)
			{
				if(item.getType() != Material.AIR)
				{
					player.getWorld().dropItem(deathLoc, item).setVelocity(new Vector(0, 0.3, 0));
				}
			}
		}
		
		this.getMatch().spawn(player);
		e.getDrops().clear();
		e.setDroppedExp(0);
		e.setDeathMessage(team.getPrefix() + player.getName() + ChatColor.WHITE + " was slain by " + player.getKiller() );
	}
}
