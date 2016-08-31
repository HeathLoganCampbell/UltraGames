package com.bevelio.ultragames.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.core.MatchState;
import com.bevelio.ultragames.events.MatchStateChangeEvent;
import com.bevelio.ultragames.plugin.BevelioPlugin;

public class Killstreak implements Listener
{
	private MatchManager mm;
	private HashMap<UUID, Integer> killstreak;
	
	public Killstreak()
	{
		mm = BevelioPlugin.getMatchManager();
		killstreak = new HashMap<>();
	}
	
	@EventHandler
	public void onStateChagne(MatchStateChangeEvent e)
	{
		if(e.getTo() == MatchState.FINISHING)
		{
			this.killstreak.clear();
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		UUID uuid = player.getUniqueId();

		if(mm.getMatch() == null)
		{
			return;
		}
		
		if(!mm.isPlaying(player))
		{
			return;
		}
		
		if(this.killstreak.containsKey(uuid))
		{
			int killstreak = this.killstreak.get(uuid);
			if(killstreak >= 3)
			{
				Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + player.getName() + " was shut down at killstreak of " + this.killstreak.get(uuid) + " kills");
			}
			this.killstreak.remove(uuid);
		}
		
		if(player.getKiller() == null)
		{
			return;
		}
		Player killer = player.getKiller();
		UUID killerUUID = killer.getUniqueId();
		
		int kills = 0;
		if(this.killstreak.containsKey(killerUUID))
		{
			kills = this.killstreak.get(killerUUID);
		}
		
		this.killstreak.put(killerUUID, ++kills);
		
		if(kills == 3 || kills % 5 == 0)
		{
			Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + killer.getName() + " has a killstreak of " + kills + " kills");
		}
		
		switch(kills)
		{
			case 3:
				player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
				break;
			case 10:
				player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
				break;
		}
	}
}
