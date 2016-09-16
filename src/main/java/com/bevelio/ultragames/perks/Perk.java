package com.bevelio.ultragames.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.kit.Kit;
import com.bevelio.ultragames.plugin.BevelioPlugin;

public class Perk implements Listener
{
	private String name;
	private String description;
	
	public Perk(String name, String description) 
	{
		this.name = name;
		this.description = description;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public String getDescription() 
	{
		return description;
	}
	
	public boolean hasPerk(Player player)
	{
		MatchManager mm = BevelioPlugin.getMatchManager();
		if(mm.getMatch() == null) return false;
		if(!mm.isPlaying(player)) return false;
		Kit kit = mm.getMatch().getPlayersKit(player.getUniqueId());
		if(kit == null) return false;
		return kit.hasPerk(this);
	}
}
