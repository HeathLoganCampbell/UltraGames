package com.bevelio.ultragames.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.plugin.BevelioPlugin;

public class BlockListener implements Listener
{
	private MatchManager mm;
	
	public BlockListener()
	{
		mm = BevelioPlugin.getMatchManager();
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		Match game = mm.getMatch();
		if(game == null) return;
		if(block == null) return;
		if(game.blockedPlaceables.contains(block.getType())) {
			e.setCancelled(true);
			return;
		}
		e.setCancelled(!game.blockPlace);
		if(game.placeables.contains(block.getType()))
			e.setCancelled(false);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Match game = mm.getMatch();
		if(game == null) return;
		if(block == null) return;
		if(game.blockedBreakables.contains(block.getType())) {
			e.setCancelled(true);
			return;
		}
		e.setCancelled(!game.blockBreak);
		if(game.breakables.contains(block.getType()))
			e.setCancelled(false);
	}
	
	@EventHandler
	public void onItem(PlayerDropItemEvent e) {
		Item item = e.getItemDrop();
		Match game = mm.getMatch();
		if(game == null) return;
		if(item == null) return;
		
		if(game.blockedDroppables.contains(item.getType())) {
			e.setCancelled(true);
			return;
		}
		
		e.setCancelled(game.itemDrop);
		
		if(game.droppables.contains(item.getType()))
			e.setCancelled(false);
	}
}
