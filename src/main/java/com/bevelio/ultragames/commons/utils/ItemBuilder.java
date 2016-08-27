package com.bevelio.ultragames.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder extends ItemStack
{
	public ItemBuilder(Material material) 
	{
		super(material);
	}
	
	public ItemBuilder setDisplayName(String displayName)
	{
		ItemMeta im = this.getItemMeta();
		im.setDisplayName(displayName);
		this.setItemMeta(im);
		return this;
	}
	
	public ItemBuilder setLore(ArrayList<String> loreList)
	{
		ItemMeta im = this.getItemMeta();
		im.setLore(loreList);
		this.setItemMeta(im);
		return this;
	}
	
	public ItemBuilder setLore(String[] loreList)
	{
		this.setLore((ArrayList<String>) Arrays.asList(loreList));
		return this;
	}
}
