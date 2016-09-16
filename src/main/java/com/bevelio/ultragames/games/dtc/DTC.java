package com.bevelio.ultragames.games.dtc;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;

import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.core.Objective;
import com.bevelio.ultragames.team.Team;

/**
 * Destroy The Core Minigame
 * 
 * Note: spawn gariadent that protects the core
 * 
 * @author Bevelio
 */
public class DTC extends Match
{
	
	public DTC()
	{
		super("DTC", new String[] {"Destroy the other teams Core", "By leaking the lava within it", "Last team standing wins!"});
	}
	
	@Override
	public void onStart()
	{
		updateScoreboard();
	}
	
	public void updateScoreboard()
	{
		for(String entry : this.getScoreboard().getScoreboard().getEntries())
		{
			this.getScoreboard().getScoreboard().resetScores(entry);
		}
		
		Iterator<Team> itTeam = this.getAllTeams().iterator();
		int i = 15;
		while(itTeam.hasNext())
		{
			Team team = itTeam.next();
			this.getScoreboard().getScore(team.getPrefix().toString() + ChatColor.BOLD.toString() + team.getName()).setScore(i--);
			
			for(Objective obj : this.getObjectives())
			{
				if(obj.teamName.equalsIgnoreCase(team.getName()))
				{
					String indient = "  ";
					String active = ChatColor.GREEN + "✔ ";
					String deactive = ChatColor.RED + "✘ ";
					this.getScoreboard().getScore(indient + (obj.active ? active :  deactive) + team.getPrefix() + obj.name).setScore(i--);
				}
			}
		}
	}
	
	@EventHandler
	public void onCoreBreak(BlockBreakEvent e)
	{
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(!this.isLive())
		{
			return;
		}
		
		
		Team playerTeam = this.getTeam(player);
		for(Objective obj : this.getObjectives())
		{
			if(obj.isWithin(block.getLocation()))
			{
				Team team = this.getTeam(obj.teamName);
				if(playerTeam == team)
				{
					player.sendMessage(ChatColor.RED + "You cannot leak your own core!");
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onLavaFlow(BlockFromToEvent e)
	{
		Block block = e.getBlock();
		if(!this.isLive())
		{
			return;
		}
		
		if(!block.getType().isSolid())
		{
			if(block.getType() == Material.LAVA
					|| block.getType() == Material.STATIONARY_LAVA)
			{
				for(Objective obj : this.getObjectives())
				{
					if(obj.isWithin(block.getLocation(), obj.radius))
					{
						obj.active = false;
						updateScoreboard();
						if(this.getRemainingTeams().size() <= 1)
						{
							this.end(this.getRemainingTeams().get(0));
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void generateObjective(Objective objective)
	{
		int radius = objective.radius;
		boolean hollow = true;
		   
		int bx = objective.location.getBlockX();
		int by = objective.location.getBlockY();
		int bz = objective.location.getBlockZ();
	     
		for(int x = bx - radius; x <= bx + radius; x++) 
		{
			for(int y = by - radius; y <= by + radius; y++) 
			{
				for(int z = bz - radius; z <= bz + radius; z++) 
				{
					double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y))) + 2.9;
					if(distance < radius * radius)
					{
						if(!(hollow && distance < ((radius - 1) * (radius - 1)))) 
						{
							new Location(objective.location.getWorld(), x, y, z).getBlock().setType(objective.material);
						}
						else 
						{
							new Location(objective.location.getWorld(), x, y, z).getBlock().setType(Material.STATIONARY_LAVA);
						}
					}
	                 
				}
			}
		}
	}
}
