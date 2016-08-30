package com.bevelio.ultragames.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.inventory.Inventory;

import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.plugin.BevelioPlugin;

public class BlockDamage implements Listener
{
	private MatchManager mm;
	
	public BlockDamage()
	{
		mm = BevelioPlugin.getMatchManager();
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		Match game = mm.getMatch();
		if(game == null) return;
		if(block == null) return;
	
		if(!mm.isPlaying(e.getPlayer()))
		{
			if(e.getPlayer().hasPermission("ultragames.block.place"))
			{
				return;
			}
			else
			{
				e.setCancelled(true);
			}
		} 
		else
		{
			if(game.blockedPlaceables.contains(block.getType())) {
				e.setCancelled(true);
				return;
			}
			e.setCancelled(!game.blockPlace);
			if(game.placeables.contains(block.getType()))
				e.setCancelled(false);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Match game = mm.getMatch();
		if(game == null) return;
		if(block == null) return;
		if(!mm.isPlaying(e.getPlayer()))
		{
			if(e.getPlayer().hasPermission("ultragames.block.place"))
			{
				return;
			}
			else
			{
				e.setCancelled(true);
			}
		}
		else 
		{
			if(game.blockedBreakables.contains(block.getType())) {
				e.setCancelled(true);
				return;
			}
			e.setCancelled(!game.blockBreak);
			if(game.breakables.contains(block.getType()))
				e.setCancelled(false);
		}
	}
	
	@EventHandler
	public void onItem(PlayerDropItemEvent e) {
		Item item = e.getItemDrop();
		Match game = mm.getMatch();
		if(game == null) return;
		if(item == null) return;
		if(!mm.isPlaying(e.getPlayer()))
		{
			if(e.getPlayer().hasPermission("ultragames.block.place"))
			{
				return;
			}
			else
			{
				e.setCancelled(true);
			}
		} 
		else
		{
			if(!game.itemDrop)
			{
				if(game.blockedDroppables.contains(item.getType())) {
					e.setCancelled(true);
					return;
				}
				
				if(game.droppables.contains(item.getType()))
					e.setCancelled(false);
			}
		}
	}
	
	@EventHandler
	public void onChest(InventoryClickEvent e) 
	{
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		
		Match game = mm.getMatch();
		if(game == null) return;
		if(player.hasPermission("ultragames.chest.edit"))
		{
			return;
		}
		if(!mm.isPlaying(player))
		{
			if(inv.getHolder() instanceof Chest || inv.getHolder() instanceof DoubleChest)
			{
				e.setCancelled(true);
			}
		}
	}
}
