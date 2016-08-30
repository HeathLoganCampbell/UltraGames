package com.bevelio.ultragames.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.core.Objective;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class TargetCompass implements Listener
{
	private MatchManager mm;
	private HashMap<UUID, Integer> target; 
	
	public TargetCompass()
	{
		mm = BevelioPlugin.getMatchManager();
		this.target = new HashMap<>();
	}
	
	@EventHandler
	public void onInteraction(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		ItemStack item = e.getItem();

		if(item == null)
		{
			return;
		}
		
		if(item.getType() == null)
		{
			return;
		}
		
		if(item.getType() != Material.COMPASS)
		{
			return;
		}
		
		if(mm.getMatch().getObjectives() == null 
				|| mm.getMatch().getObjectives().isEmpty() 
				|| mm.getMatch().getObjectives().size() == 0)
		{
			return;
		}
		
		if(e.getAction().name().contains("RIGHT_CLICK"))
		{
			int targetId = 0;
			if(this.target.containsKey(uuid))
			{
				targetId = this.target.get(uuid);
			}
			this.target.put(uuid, ++targetId);
			List<Objective> objs = mm.getMatch().getObjectives();
			Objective obj = objs.get(targetId % objs.size());
			player.setCompassTarget(obj.location);
			ChatColor color = mm.getMatch().getTeam(obj.teamName).getPrefix();
			player.sendMessage(ChatColor.YELLOW + "Compass is now pointing at " + color + obj.name + ChatColor.YELLOW + ".");
		}
	}
}
