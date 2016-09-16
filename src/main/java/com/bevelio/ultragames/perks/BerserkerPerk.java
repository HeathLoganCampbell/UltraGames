package com.bevelio.ultragames.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class BerserkerPerk extends Perk
{
	private String berserkerRageMessage = ChatColor.RED + "-== A lust fills your body ==-";
	
	public BerserkerPerk() 
	{
		super("Berserker", "Get strength after killing a player");
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		Player killer = player.getKiller();
		if(!this.hasPerk(killer)) return;
		
		killer.sendMessage(berserkerRageMessage);
		killer.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 1));
		killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 2));
	}
}
