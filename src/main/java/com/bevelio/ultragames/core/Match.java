package com.bevelio.ultragames.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.bevelio.ultragames.commons.Settings;
import com.bevelio.ultragames.commons.utils.GearUtils;
import com.bevelio.ultragames.commons.utils.PlayerUtils;
import com.bevelio.ultragames.kit.Kit;
import com.bevelio.ultragames.map.WorldData;
import com.bevelio.ultragames.team.Team;

public class Match implements Listener
{
	private String 						name;
	private String[] 					description;
	private WorldData 					worldData;
	
	private Team						winningTeam;
	
	public boolean						midGameJoin			= true
				,						freeForAll			= false;
	
	public boolean 						blockBreak 			= true
				, 						blockPlace 			= true
				, 						itemDrop 			= true;

	public HashSet<Material> 			breakables 			= new HashSet<>()
						   , 			placeables 			= new HashSet<>()
						   , 			droppables 			= new HashSet<>()
						   , 			blockedBreakables 	= new HashSet<>()
						   , 			blockedPlaceables 	= new HashSet<>()
						   , 			blockedDroppables 	= new HashSet<>();
	
	public boolean 						chestOnInventory 	= true
		     				, 			blockInteract 		= true
		     				, 			paintKnockOff 		= false
		
		     				, 			worldFireSpread 	= true
		     				, 			worldLeavesDecay 	= true
		     				, 			worldBlockBurn 		= true
		     				, 			worldSoilTrample 	= true
		     				, 			worldWeather 		= false
		     				, 			worldDayNightCycle 	= false;
	
	public int 							worldTime 			= 12000;
	
	public boolean 						creatureAllow = false
				, 						creatureAllowForce = false;
	
	private HashMap<String, Kit>		kits;
	private HashMap<String, Team> 		teams;
	private HashMap<String, Spawn>		spawns;
	
	public Match(String name, String[] description)
	{
		this.name = name;
		this.description = description;
		
		this.kits = new HashMap<>();
		this.teams = new HashMap<>();
		this.spawns = new HashMap<>();
	}

	public String getName()
	{
		return name;
	}

	public String[] getDescription()
	{
		return description;
	}

	public WorldData getWorldData()
	{
		return worldData;
	}

	public void setWorldData(WorldData worldData)
	{
		this.worldData = worldData;
	}
	
	public void addKit(Kit kit)
	{
		this.kits.put(kit.getName().toLowerCase(), kit);
	}
	
	public Kit getKit(String name)
	{
		return this.kits.get(name.toLowerCase());
	}
	
	public void startingAnnouncement()
	{
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.BOLD + " " + this.getWorldData().displayName + " (" + this.getName() + ")");
		Bukkit.broadcastMessage(ChatColor.BOLD + " by " + this.getWorldData().authors );
		Bukkit.broadcastMessage("");
		for(String description : this.getDescription())
		{
			Bukkit.broadcastMessage(ChatColor.GRAY + "  " + description);
		}
		Bukkit.broadcastMessage("");
	}
	
	public void endingAnnouncement(Team winningTeam)
	{
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.BOLD + " " + winningTeam.getName() + " wins");
		Bukkit.broadcastMessage("");
		for(String description : this.getDescription())
		{
			Bukkit.broadcastMessage(ChatColor.GRAY + "  " + description);
		}
		Bukkit.broadcastMessage("");
	}
	
	public void createTeam(Team team)
	{
		this.teams.put(team.getName().toLowerCase(), team);
	}
	
	public Collection<Team> getAllTeam()
	{
		return this.teams.values();
	}
	
	public Team getTeam(String name)
	{
		return this.teams.get(name);
	}
	
	public Team getTeam(Player player)
	{
		for(Team team : this.getAllTeam())
		{
			if(team.isMember(player.getUniqueId()))
			{
				return team;
			}
		}
		return null;
	}
	
	public Team selectTeam(Team prefrenceTeam)
	{
		Team bestTeam = null;
		for(Team team : this.getAllTeam())
		{
			if(bestTeam == null
					|| team.size() < bestTeam.size())
			{
				bestTeam = team;
			}
		}
		
		if(prefrenceTeam != null)
		{
			if(prefrenceTeam.size() - bestTeam.size() <= Settings.TEAM_SIZE_PADDING)
			{
				return prefrenceTeam;
			}
		}
		
		return bestTeam;
	}
	
	public void addNewSpawn(Spawn spawn)
	{
		this.spawns.put(spawn.getName().toLowerCase(), spawn);
	}
	
	public Spawn getSpawn(String name)
	{
		return this.spawns.get(name.toLowerCase());
	}
	
	public void spawn(Player player)
	{
		Team team = this.getTeam(player);
		if(team == null)
		{
			return;
		}
		player.eject();
		String spawnName = team.getSpawnNames().get(0);
		System.out.println("Spawn ]=> " + spawnName);
		Spawn spawn = this.getSpawn(spawnName);
		player.teleport(spawn.getSpawn());
		PlayerUtils.reset(player, true);
		
		if(spawn.getDefaultKit() == null)
		{
			player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
			player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
			player.getInventory().addItem(new ItemStack(Material.BOW));
			player.getInventory().setItem(9 ,new ItemStack(Material.ARROW, 32));
			
			ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta im = (LeatherArmorMeta) chestplate.getItemMeta();
			im.setColor(GearUtils.translateChatColorToColor(team.getPrefix()));
			chestplate.setItemMeta(im);
			
			player.getInventory().setChestplate(chestplate);
		} 
		else
		{
			Kit kit = this.getKit(spawn.getDefaultKit());
			if(kit == null)
			{
				player.sendMessage(ChatColor.GRAY + "Failed to apply kit " + team.getDefaultKit());
				return;
			}
			
			kit.apply(player);
		}
	}
	
	public void onStart() {}
	public void onEnd() {}
	
	public void start()
	{
		List<ChatColor> colors = Arrays.asList(new ChatColor[] { ChatColor.BLUE, ChatColor.RED, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.GOLD, ChatColor.DARK_PURPLE});
		
		for(Team team : this.getWorldData().teams)
		{
			this.createTeam(team);
		}
		
		for(Spawn spawn : this.getWorldData().spawns)
		{
			System.out.println("New Spawn ]=> " + spawn.getName());
			spawn.getSpawn().setWorld(this.worldData.world);
			this.addNewSpawn(spawn);
		}
		
		this.onStart();
		this.startingAnnouncement();
		
	}
	
	public void end(Team winningTeam)
	{
		this.winningTeam = winningTeam;
		this.endingAnnouncement(winningTeam);
		this.onEnd();
	}
}
