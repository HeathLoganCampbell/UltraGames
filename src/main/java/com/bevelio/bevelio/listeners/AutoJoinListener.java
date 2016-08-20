package com.bevelio.bevelio.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.bevelio.bevelio.core.MatchManager;
import com.bevelio.bevelio.plugin.BevelioPlugin;
import com.bevelio.bevelio.team.Team;

public class AutoJoinListener implements Listener
{
	private MatchManager mm;
	
	public AutoJoinListener()
	{
		mm = BevelioPlugin.getMatchManager();
	}
	
	@EventHandler
	public void onInteraction(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
		
		if(item == null)
		{
			return;
		}
		
		if(item.getType() == null)
		{
			return;
		}
		
		if(item.getType() != Material.SLIME_BALL)
		{
			return;
		}
		
		if(e.getAction().name().contains("RIGHT_CLICK"))
		{
			Team team = mm.joinMatch(player);
			player.sendMessage(ChatColor.GREEN + "You have joinned " + team.getDisplayName() + ChatColor.GREEN + "!");
		}
	}
}
