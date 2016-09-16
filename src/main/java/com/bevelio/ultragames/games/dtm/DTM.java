package com.bevelio.ultragames.games.dtm;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.core.Objective;
import com.bevelio.ultragames.team.Team;

/**
 * Destroy The Core Minigame
 * 
 * 
 * @author Bevelio
 */
public class DTM extends Match
{
	
	public DTM()
	{
		super("DTM", new String[] {"Destroy the other teams Monument", "By leaking the lava within it", "Last team standing wins!"});
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
	public void onBlockBreak(BlockBreakEvent e)
	{
		Block block = e.getBlock();
		if(!this.isLive())
		{
			return;
		}
		
		if(block == null)
		{
			return;
		}
		
		if(block.getType() == Material.OBSIDIAN)
		{
			
			for(Objective obj : this.getObjectives())
			{
				if(block.getLocation().getBlockX() == obj.location.getBlockX()
						&& block.getLocation().getBlockY() == obj.location.getBlockY()
						&& block.getLocation().getBlockZ() == obj.location.getBlockZ())
				{
					int blocksLeft = 0;
					for(BlockFace blockFace : BlockFace.values())
					{
						if(block.getRelative(blockFace).getType() == Material.OBSIDIAN)
						{
							blocksLeft++;
						}
					}
					
					if(blocksLeft == 0)
					{
						obj.active = false;
						if(this.getRemainingTeams().size() <= 1)
						{
							this.end(this.getRemainingTeams().get(0));
						}
					}
					else
					{
						Team team = this.getTeam(obj.teamName);
						Bukkit.broadcastMessage(team.getPrefix().toString() + blocksLeft + " blocks remaining for " + obj.name);
					}
				}
			}
		}
	}
}
