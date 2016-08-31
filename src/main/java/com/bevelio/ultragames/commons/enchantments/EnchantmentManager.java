package com.bevelio.ultragames.commons.enchantments;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentManager 
{

    private static final int[] BVAL = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
    private static List<Integer> customEnchants = new ArrayList<>();

    // Parallel arrays used in the conversion process.
    private static final String[] RCODE = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

    public static Enchantment UNDROPPABLE;
    public static Enchantment UNLOOTABLE;
  //  public static Enchantment UNPLACEABLE;

    static 
    {
        UNLOOTABLE = new Unlootable(getId());
        UNDROPPABLE = new Undroppable(getId());
     //   UNPLACEABLE = new Unplaceable(getId());
        try 
        {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.setBoolean(EnchantmentManager.UNLOOTABLE, true);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        } 
        if(Enchantment.getByName(UNLOOTABLE.getName()) == null)
        {
        	Enchantment.registerEnchantment(UNLOOTABLE);
            Enchantment.registerEnchantment(UNDROPPABLE);
   //         Enchantment.registerEnchantment(UNPLACEABLE);
        } 
        else 
        {
        	UNLOOTABLE = Enchantment.getByName(UNLOOTABLE.getName());
        	UNDROPPABLE = Enchantment.getByName(UNDROPPABLE.getName());
     //  	UNPLACEABLE = Enchantment.getByName(UNPLACEABLE.getName());
        }
        customEnchants.add(UNLOOTABLE.getId());
        customEnchants.add(UNDROPPABLE.getId());
     //   customEnchants.add(UNPLACEABLE.getId());
    }

    private static int getId() 
    {
        for (int i = 1; i <= 1000; i++)
        {
            if (Enchantment.getById(i) == null && !customEnchants.contains(i)) 
            {
                customEnchants.add(i);
                return i;
            }
        }
        return 0;
    }

    public static boolean isNatural(Enchantment ench) 
    {
        if (customEnchants.contains(ench.getId()))
        {
            return false;
        }
        return true;
    }

    // =========================================================== binaryToRoman
    private static String toRoman(int binary) 
    {
        if (binary <= 0)
        {
            return "";
        }
        String roman = "";
        for (int i = 0; i < RCODE.length; i++) 
        {
            while (binary >= BVAL[i])
            {
                binary -= BVAL[i];
                roman += RCODE[i];
            }
        }
        return roman;
    }

    public static ItemStack updateEnchants(ItemStack item) 
    {
        ArrayList<String> enchants = new ArrayList<String>();
        for (Enchantment ench : item.getEnchantments().keySet()) 
        {
            if (!isNatural(ench)) 
            {
                String enchantName = StringUtils.capitalize(ench.getName().toLowerCase());
                enchants.add(ChatColor.GRAY + enchantName + " " + toRoman(item.getEnchantments().get(ench)));
            }
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore())
        {
            for (String lore : meta.getLore())
            {
                enchants.add(lore);
            }
        }
        meta.setLore(enchants);
        item.setItemMeta(meta);
        return item;
    }
}