package com.bevelio.bevelio.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class Kit
{
	private String name;
	private HashMap<Integer, ItemStack> items, armor;
	private List<PotionEffect> effects;
	
	public Kit(String name)
	{
		this.name = name;
		
		this.items = new HashMap<>();
		this.armor = new HashMap<>();
		
		this.effects = new ArrayList<>();
	}

	public String getName()
	{
		return name;
	}
	
	public void addItem(int slot, ItemStack item)
	{
		this.items.put(slot, item);
	}
	
	public ItemStack getItem(int slot)
	{
		return this.items.get(slot);
	}
	
	public void setArmor(ArmorType armorType, ItemStack item)
	{
		this.armor.put(armorType.getId(), item);
	}
	
	public ItemStack getArmor(ArmorType armorType)
	{
		return this.armor.get(armorType.getId());
	}
	
	public void addEffect(PotionEffect effect)
	{
		this.effects.add(effect);
	}
	
	public void apply(Player player)
	{
		PlayerInventory inv = player.getInventory();
		for(int i = 0; i < 32; i++)
		{
			inv.setItem(i, this.getItem(i));
		}
		
		inv.setHelmet(this.getArmor(ArmorType.HELMET));
		inv.setChestplate(this.getArmor(ArmorType.CHESTPLATE));
		inv.setLeggings(this.getArmor(ArmorType.LEGGINGS));
		inv.setBoots(this.getArmor(ArmorType.BOOTS));
		
		for(PotionEffect effect : this.effects)
		{
			player.addPotionEffect(effect);
		}
	}
}
