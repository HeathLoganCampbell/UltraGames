package com.bevelio.ultragames.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.plugin.BevelioPlugin;

public class Creature implements Listener
{
	private MatchManager mm;
	
	public Creature()
	{
		mm = BevelioPlugin.getMatchManager();
	}
	
	@EventHandler
	public void onMonsterTarget(EntityTargetLivingEntityEvent e) 
	{
		Match game = mm.getMatch();
		if(game == null) return;
		if(e.isCancelled()) return;
		if(!(e.getTarget() instanceof Player)) return;
		Player player = (Player) e.getTarget();
		e.setCancelled(!mm.isPlaying(player));
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onWorldCreature(CreatureSpawnEvent event) 
	{
		Match game = mm.getMatch();
		if(game == null) 
		{
			return;
		}

	    if ((!game.creatureAllow) && (!game.creatureAllowForce))
	    {
	    	event.setCancelled(true);
	    }
	}
}
