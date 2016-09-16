package com.bevelio.ultragames.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.bevelio.ultragames.perks.Perk;

public class Kit
{
	private String name;
	private HashMap<Integer, ItemStack> items, armor;
	private List<PotionEffect> effects;
	private List<Perk> perks;
	private HashSet<String> perkHash;
	
	public Kit(String name)
	{
		this.name = name;
		
		this.items = new HashMap<>();
		this.armor = new HashMap<>();
		
		this.effects = new ArrayList<>();
		this.perks = new ArrayList<>();
		this.perkHash = new HashSet<>();
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
	
	public void addPerk(Perk perk)
	{
		this.perks.add(perk);
		this.perkHash.add(perk.getName().toLowerCase());
	}
	
	public boolean hasPerk(Perk perk)
	{
		return this.perkHash.contains(perk.getName().toLowerCase());
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
