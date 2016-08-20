package com.bevelio.ultragames.games.tdm;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.team.Team;

public class TDM extends Match
{
	private HashMap<String, Integer> deaths;
	
	public TDM()
	{
		super("TDM", new String[] {"Team with the least deaths win"});
		
		this.deaths = new HashMap<>();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		Team team = this.getTeam(player);
		if(team == null)
		{
			return;
		}
		String name = team.getName();
		
		int deaths = 0;
		if(this.deaths.containsKey(name))
			deaths = this.deaths.get(name);
		this.deaths.put(name, ++deaths);
		Bukkit.broadcastMessage(team.getDisplayName() + ": " + deaths);
	}
}
