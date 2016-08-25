package com.bevelio.ultragames.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class AutoJoin implements Listener
{
	private MatchManager mm;
	
	public AutoJoin()
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
			if(team == null)
			{
				player.sendMessage(ChatColor.RED + "This game has not started yet!");
				return;
			}
			player.sendMessage(ChatColor.GREEN + "You have joinned " + team.getDisplayName() + ChatColor.GREEN + "!");
		}
	}
}
