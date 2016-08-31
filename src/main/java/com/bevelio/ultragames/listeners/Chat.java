package com.bevelio.ultragames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class Chat implements Listener
{
	private MatchManager mm;
	
	public Chat()
	{
		mm = BevelioPlugin.getMatchManager();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		Player player = e.getPlayer();
		String message = e.getMessage();
		String name = player.getName();
		String prefix = ChatColor.GRAY.toString();
		if(mm.getMatch() != null)
		{
			if(mm.isPlaying(player))
			{
				Team team = mm.getMatch().getTeam(player);
				if(team != null)
				{
					prefix += team.getPrefix().toString();
				}
			}
		}
		//String resultMessage = prefix + name + ChatColor.WHITE + "> " + message;
		String resultMessage = prefix + "<" + ChatColor.WHITE + name + prefix + "> " + ChatColor.WHITE + message;
		
		for(Player viewer : Bukkit.getOnlinePlayers())
		{
			viewer.sendMessage(resultMessage);
		}
		e.setCancelled(true);
	}
}
