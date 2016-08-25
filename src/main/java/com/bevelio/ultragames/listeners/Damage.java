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

public class Damage implements Listener 
{
	private HashMap<UUID, Long> lastDamage = new HashMap<>();
	private long damageEclipse = 380;
	
	@EventHandler
	public void onDamage(CustomDamageEvent e)
	{
		
		LivingEntity entity = null;
		if(e.getDamagedPlayer() == null)
		{
			entity = e.getDamagedEntity();
		}
		else 
		{
			entity = e.getDamagedPlayer();
		}
		UUID uuid = entity.getUniqueId();
		
		if(!lastDamage.containsKey(uuid))
		{
			lastDamage.put(uuid, System.currentTimeMillis() + damageEclipse);
			return;
		}
		
		if(lastDamage.get(uuid) >= System.currentTimeMillis())
		{
			e.setCancelled("Hitting too fast");
		} else {
			lastDamage.put(uuid, (long) (System.currentTimeMillis() + damageEclipse + (20 * (entity.getMaxHealth() - entity.getHealth()))));
		}
		
	}
}
