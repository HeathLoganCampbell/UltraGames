package com.bevelio.ultragames.games.tdm;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.core.Objective;
import com.bevelio.ultragames.team.Team;

public class TDM extends Match
{
	private HashMap<String, Integer> deaths;
	
	public TDM()
	{
		super("TDM", new String[] {"Team with the least deaths win"});
		
		this.deaths = new HashMap<>();
	}
	
	@Override
	protected Team onTimeUp()
	{
		Team bestTeam = null;
		int bestTeamDeaths = 9999;
		for(Team team : this.getAllTeam())
		{
			int deaths = this.getTeamDeaths(team.getName());
			
			if(bestTeam == null || bestTeamDeaths > deaths)
			{
				bestTeam = team;
				bestTeamDeaths = deaths;
			}
		}
		
		return bestTeam;
	}
	
	public void updateScoreboard()
	{
		for(String entry : this.getScoreboard().getScoreboard().getEntries())
		{
			this.getScoreboard().getScoreboard().resetScores(entry);
		}
		
		for(Team team : this.getAllTeam())
		{
			this.getScoreboard().getScore(team.getPrefix() + team.getName()).setScore(getTeamDeaths(team.getName()));
		}
	}
	
	public int getTeamDeaths(String team)
	{
		int deaths = this.deaths.get(team);
		if(this.deaths.containsKey(team))
		{
			return 0;
		}
		return deaths;
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
		
		int deaths = getTeamDeaths(name);
		this.deaths.put(name, ++deaths);
		updateScoreboard();
	}
}
