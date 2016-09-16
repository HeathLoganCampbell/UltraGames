package com.bevelio.ultragames.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;

import com.bevelio.ultragames.commons.Settings;
import com.bevelio.ultragames.commons.enchantments.EnchantmentManager;
import com.bevelio.ultragames.commons.updater.UpdateEvent;
import com.bevelio.ultragames.commons.updater.UpdateType;
import com.bevelio.ultragames.commons.utils.ItemBuilder;
import com.bevelio.ultragames.commons.utils.MathUtils;
import com.bevelio.ultragames.commons.utils.PlayerUtils;
import com.bevelio.ultragames.commons.utils.WorldUtils;
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
		//Bukkit.broadcastMessage(ChatColor.GRAY + "MatchState]: " + this.state.name() + " => " + matchState.name());
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
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add("Right click this in your hand");
		lore.add("to join the current game");
		
		inv.setItem(0, new ItemBuilder(Material.SLIME_BALL)
										.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Click to join!")
										.setLore(lore)
					);
		player.setAllowFlight(true);
		player.setFlying(true);
	}
	
	public void addSpectator(Player player)
	{
		this.spectators.add(player.getUniqueId());
		player.setGameMode(GameMode.CREATIVE);
		this.setSpectatorInventory(player);
		player.spigot().setCollidesWithEntities(false);
		Bukkit.getOnlinePlayers().forEach(viewer ->
		{
			if(viewer != player) return;
			if(!this.spectators.contains(viewer.getUniqueId()))
				viewer.hidePlayer(player);
			player.showPlayer(viewer);
		});
	}
	
	public void addAutoJoin(UUID uuid)
	{
		autoJoin.add(uuid);
	}
	
	public boolean isAutoJoin(UUID uuid)
	{
		return autoJoin.contains(uuid);
	}
	
	public void leaveAutoJoin(UUID uuid)
	{
		autoJoin.remove(uuid);
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
		
		if(this.getState() != MatchState.LIVE)
		{
			return null;
		}
		
		player.spigot().setCollidesWithEntities(true);
		team.addMember(player.getUniqueId());
		team.getBukkitTeam().addEntry(player.getName());
		this.match.spawn(player);
		this.removeSpectator(player);
		return team;
	}
	
	public boolean isValidWorld(String worldName)
	{
		return this.worldManager.getWorld(worldName) != null;
	}
	
	public Match createMatch(String worldName)
	{
		WorldData worldData = this.worldManager.createNewWorld(++this.id, worldName);
		
		Match match = null;
		
		try
		{
	        File mapFile = new File(worldData.world.getName() + File.separator  + "Map.yml");
			System.out.println(mapFile.toString());
			ConfigurationSection config = YamlConfiguration.loadConfiguration(mapFile);
	        
	        BevelioPlugin.getConfigManager().loadMap(config, worldData);
	        
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
					return this.createMatch(worldName);
				}
			}
	        
	        match = matchClazz.newInstance();
	        
	        
	        File kitFile = new File(worldData.world.getName() + File.separator + "Kits.yml");
			System.out.println(kitFile.toString());
	        config = YamlConfiguration.loadConfiguration(kitFile);
	        for(Kit kit : BevelioPlugin.getConfigManager().loadKits(config))
	        {
	        	match.addKit(kit);
	        	System.out.println("Kit loaded " + kit.getName());
	        }
	        
			match.setWorldData(worldData);
	        
		} 
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return match;
	}
	
	public void leaveAllTeams(Player player)
	{
		for(Team team : this.match.getAllTeams())
		{
			if(team.isMember(player.getUniqueId()))
			{
				team.removeMember(player.getUniqueId());
				team.getBukkitTeam().removeEntry(player.getName());
			}
		}
	}
	
	public void setNextMatch(Match match)
	{
		this.nextMatch = match;
	}
	
	public void leaveGame(Player player)
	{
		this.removeSpectator(player);
		this.leaveAllTeams(player);
	}
	
	public void setLaunchFireworks(Location location, ChatColor chatcolor)
	{
		Color color = WorldUtils.translateChatColorToColor(chatcolor);
		Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        
        Type type = Type.BALL;
        int ran =  MathUtils.getRandom(4);
        if(ran == 0)
        {
        	type = Type.BALL_LARGE;
        } 
        else if(ran == 1)
        {
        	type = Type.BURST;
        }
        
        FireworkEffect effect = FireworkEffect.builder()
        									  .flicker(true)
        									  .withColor(color)
        									  .withFade(color)
        									  .with(type)
        									  .trail(true)
        								.build();
        fwm.addEffect(effect);
        int rp = MathUtils.getRandom(2) + 1;
        fwm.setPower(rp);
        
        fw.setFireworkMeta(fwm);
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e)
	{
		String MOTD = ChatColor.BOLD + "           Bevelio.com\n";
		if(this.getState() == MatchState.LIVE)
		{
			WorldData worldData = this.getMatch().getWorldData();
			String matchPlaying = worldData.displayName + " " + worldData.gameType;
			MOTD += ChatColor.DARK_AQUA + "        Now Playing " + matchPlaying;
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
		
		if(this.getMatch() != null)
		{
			player.setScoreboard(this.getMatch().getScoreboard().getScoreboard());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		e.setQuitMessage(null);
		leaveGame(player);
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e)
	{
		Player player = e.getPlayer();
		leaveGame(player);
	}
	
	
	@EventHandler
	public void onStateChange(MatchStateChangeEvent e)
	{
		if(e.getTo() == MatchState.FINISHING || e.getFrom() == MatchState.LOADING)
		{
			this.nextMatch = createMatch(null);
		}
				
		if(e.getTo() == MatchState.WAITING)
		{
			this.match = this.nextMatch;
			World world = Bukkit.getWorld(Settings.GAME_WORLD_NAME + this.id);
			Bukkit.getOnlinePlayers().forEach(player -> player.teleport(world.getSpawnLocation()));
			this.worldManager.deleteMatch(this.id - 1);
		}
		
		if(e.getTo() == MatchState.FINISHING)
		{
			HandlerList.unregisterAll(this.match);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				for(Player viewer : Bukkit.getOnlinePlayers())
				{
					viewer.showPlayer(player);
				}
				player.setGameMode(GameMode.CREATIVE);
				this.leaveAllTeams(player);
				this.spectators.add(player.getUniqueId());
				this.setSpectatorInventory(player);
				player.setAllowFlight(true);
				player.setFlying(true);
			}
		}
		
		if(e.getTo() == MatchState.LIVE)
		{
			Bukkit.getPluginManager().registerEvents(this.match, BevelioPlugin.getInstance());
			for(Player player : Bukkit.getOnlinePlayers())
			{
				if(this.isAutoJoin(player.getUniqueId()))
				{
					Team team = BevelioPlugin.getMatchManager().joinMatch(player);
					player.sendMessage(ChatColor.GREEN + "You have joinned " + team.getDisplayName() + ChatColor.GREEN + "!");
				}
			}	
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
					Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in " + ChatColor.RED + this.getSeconds() + ChatColor.GREEN + " second" + (this.getSeconds() == 1 ? "" : "s") + "!");
				if(this.getSeconds() <= 1)
				{
					this.match.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(this.match.getWorldData().gameType + MathUtils.getRandom(9999), "dummy"));
					this.match.getScoreboard().setDisplaySlot(DisplaySlot.SIDEBAR);
					this.match.start();
					for(Team team : this.match.getAllTeams())
					{
						org.bukkit.scoreboard.Team bukkitTeam = this.match.getScoreboard().getScoreboard().registerNewTeam(team.getName());
						bukkitTeam.setPrefix(team.getPrefix().toString());
						team.setBukkitTeam(bukkitTeam);
					}
					for(Player player : Bukkit.getOnlinePlayers())
					{
						player.setScoreboard(this.match.getScoreboard().getScoreboard());
					}
				}
				break;
			case LIVE:
			
				this.match.getScoreboard().setDisplayName(ChatColor.BOLD + this.match.getName() + " " + this.match.toFormateTime());
				if(this.getSeconds() <= 1)
				{
					
				}
				break;
			case FINISHING:
				if(this.getMatch().getWinningTeam() != null)
				{
					ChatColor teamColor = this.getMatch().getWinningTeam().getPrefix();
					for(Chunk chunk : this.getMatch().getWorld().getLoadedChunks())
					{
						if(MathUtils.getRandom(10) <= 5)
						{
							Block block = chunk.getBlock(MathUtils.getRandom(15), 0, MathUtils.getRandom(15));
							Location highestBlock = this.getMatch().getWorld().getHighestBlockAt(block.getLocation()).getLocation();
							this.setLaunchFireworks(highestBlock, teamColor);
						}
					}
				}
				if(this.seconds < 5 || this.seconds % 5 == 0)
					Bukkit.broadcastMessage(ChatColor.GREEN + "Game ending in " + ChatColor.RED + this.getSeconds() + ChatColor.GREEN + " second" + (this.getSeconds() == 1 ? "" : "s") + "!");
				if(this.getSeconds() <= 1)
				{
					if(match.getScoreboard() != null)
					{
						match.getScoreboard().unregister();
					}
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
					if(item.getEnchantmentLevel(EnchantmentManager.UNLOOTABLE) == 0 && (!this.match.getWorldData().itemRemoves.contains(item.getType())))
					{
						player.getWorld().dropItem(deathLoc, item).setVelocity(new Vector(0, 0.3, 0));
					}
				}
			}
		}
		
		
		e.getDrops().clear();
		e.setDroppedExp(0);
		if(player.getKiller() != null)
		{
			ChatColor killerChatColor = ChatColor.WHITE;
			Team killerTeam = this.getMatch().getTeam(player.getKiller());
			if(killerTeam != null)
			{
				killerChatColor = killerTeam.getPrefix();
			}
			
			e.setDeathMessage(team.getPrefix() + player.getName() + ChatColor.WHITE + " was slain by " + killerChatColor + player.getKiller().getName() );
		} 
		else 
		{
			e.setDeathMessage(team.getPrefix() + player.getName() + ChatColor.WHITE + " died!");
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(BevelioPlugin.getInstance(), () ->
		{
			this.getMatch().spawn(player);
		}, 1l);
	}
}
