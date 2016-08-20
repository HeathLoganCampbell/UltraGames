package com.bevelio.ultragames.commons.utils;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GearUtils 
{
private static HashMap<Material, Double> armor;
	
	public static boolean isWeapon(ItemStack item) 
	{
		if(item == null) 
		{
			return false;
		}
		if(item.getType() == Material.AIR) 
		{
			return false;
		}
		
		String name = item.getType().name();
		return name.contains("_SWORD") 
				|| name.contains("_AXE") 
				|| name.contains("_PICKAXE");
	}
	
	public static double getArmorPoint(Material material) 
	{
		if(armor == null) 
		{
			armor.put(Material.LEATHER_HELMET, 0.5);
			armor.put(Material.LEATHER_CHESTPLATE, 1.5);
			armor.put(Material.LEATHER_LEGGINGS, 1.0);
			armor.put(Material.LEATHER_BOOTS, 0.5);
			
			armor.put(Material.CHAINMAIL_HELMET, 1.0);
			armor.put(Material.CHAINMAIL_CHESTPLATE, 2.5);
			armor.put(Material.CHAINMAIL_LEGGINGS, 2.0);
			armor.put(Material.CHAINMAIL_BOOTS, 1.0);

			armor.put(Material.GOLD_HELMET, 1.0);
			armor.put(Material.GOLD_CHESTPLATE, 2.5);
			armor.put(Material.GOLD_LEGGINGS, 2.0);
			armor.put(Material.GOLD_BOOTS, 1.0);
			
			armor.put(Material.IRON_HELMET, 1.0);
			armor.put(Material.IRON_CHESTPLATE, 3.0);
			armor.put(Material.IRON_LEGGINGS, 2.5);
			armor.put(Material.IRON_BOOTS, 1.0);
			
			armor.put(Material.DIAMOND_HELMET, 1.5);
			armor.put(Material.DIAMOND_CHESTPLATE, 4.0);
			armor.put(Material.DIAMOND_LEGGINGS, 3.0);
			armor.put(Material.DIAMOND_BOOTS, 1.5);
		}
		return armor.containsKey(material) ? armor.get(material) : 0;
	}
	
	public static double getArmorPoints(Player player)
	{
		int armorPoints = 0;
		
		ItemStack[] armor = player.getInventory().getArmorContents();
		for(int i = 0; i < armor.length; i++)
		{
			if(armor[i] == null)
			{
				armorPoints += getArmorPoint(armor[i].getType());
			}
		}
		return armorPoints;
	}
	
	public static Color getColor(ChatColor color)
	{
		switch(color)
		{
		case AQUA:
			return Color.AQUA;
		case BLACK:
			return Color.BLACK;
		case BLUE:
			return Color.BLUE;
		case BOLD:
			break;
		case DARK_AQUA:
			return Color.AQUA;
		case DARK_BLUE:
			return Color.BLUE;
		case DARK_GRAY:
			break;
		case DARK_GREEN:
			break;
		case DARK_PURPLE:
			break;
		case DARK_RED:
			break;
		case GOLD:
			return Color.YELLOW;
		case GRAY:
			break;
		case GREEN:
			return Color.GREEN;
		case ITALIC:
			break;
		case LIGHT_PURPLE:
			break;
		case MAGIC:
			break;
		case RED:
			return Color.RED;
		case RESET:
			break;
		case STRIKETHROUGH:
			break;
		case UNDERLINE:
			break;
		case WHITE:
			return Color.WHITE;
		case YELLOW:
			return Color.YELLOW;
		default:
			break;
		
		}
		return Color.RED;
	}
	
	public static Color translateChatColorToColor(ChatColor chatColor)
    {
        switch (chatColor) {
            case AQUA:
                return Color.AQUA;
            case BLACK:
                return Color.BLACK;
            case BLUE:
                return Color.BLUE;
            case DARK_AQUA:
                return Color.BLUE;
            case DARK_BLUE:
                return Color.BLUE;
            case DARK_GRAY:
                return Color.GRAY;
            case DARK_GREEN:
                return Color.GREEN;
            case DARK_PURPLE:
                return Color.PURPLE;
            case DARK_RED:
                return Color.RED;
            case GOLD:
                return Color.YELLOW;
            case GRAY:
                return Color.GRAY;
            case GREEN:
                return Color.GREEN;
            case LIGHT_PURPLE:
                return Color.PURPLE;
            case RED:
                return Color.RED;
            case WHITE:
                return Color.WHITE;
            case YELLOW:
                return Color.YELLOW;
            default:
            break;
        }
       
        return null;
    }
	
	public static ItemStack setItemAndLore(ItemStack item, String name, String[] lore)
	{
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(Arrays.asList(lore));
		item.setItemMeta(itemMeta);
		return item;
	}
}
