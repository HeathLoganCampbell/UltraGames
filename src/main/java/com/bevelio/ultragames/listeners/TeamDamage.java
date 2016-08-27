package com.bevelio.ultragames.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bevelio.ultragames.commons.damage.CustomDamageEvent;
import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class TeamDamage implements Listener 
{
	private MatchManager mm;
	
	public TeamDamage()
	{
		mm = BevelioPlugin.getMatchManager();
	}
	
	@EventHandler
	public void onDamage(CustomDamageEvent e)
	{
		Player player = e.getDamagedPlayer();
		Player killer = e.getDamagerPlayer(true);
		if(player == null || killer == null)
		{
			return;
		}
		
		if(!(mm.isPlaying(player) || mm.isPlaying(killer)))
		{
			return;
		}
		
		Team playerTeam = mm.getMatch().getTeam(player);
		Team killerTeam = mm.getMatch().getTeam(killer);
		
		if(playerTeam == killerTeam)
		{
			e.setCancelled("You cannot attack team mates!");
		}
	}
}
