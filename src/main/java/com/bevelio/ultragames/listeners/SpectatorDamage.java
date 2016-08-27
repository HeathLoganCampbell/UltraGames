package com.bevelio.ultragames.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.bevelio.ultragames.commons.damage.CustomDamageEvent;
import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.plugin.BevelioPlugin;

public class SpectatorDamage implements Listener
{
	private MatchManager mm;
	
	public SpectatorDamage()
	{
		mm = BevelioPlugin.getMatchManager();
	}
	
	@EventHandler
	public void onDamage(CustomDamageEvent e)
	{
		if(e.getDamagedPlayer() == null)
		{
			return;
		}
		Player player = e.getDamagedPlayer();
		
		if(mm.isPlaying(player))
		{
			return;
		}
		
		if(e.getCause() == DamageCause.VOID)
		{
			player.teleport(player.getWorld().getSpawnLocation());
			player.sendMessage(ChatColor.BOLD + "You can't leave the world silly");
		}
	}
}
