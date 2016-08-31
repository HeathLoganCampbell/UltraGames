package com.bevelio.ultragames.commons.enchantments;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Unplaceable extends Enchantment
{

    public Unplaceable(int id) 
    {
        super(id);
    }

    @Override
    public boolean canEnchantItem(ItemStack item) 
    {
        return true;
    }

    @Override
    public boolean conflictsWith(Enchantment other) 
    {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() 
    {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public int getMaxLevel() 
    {
        return 1;
    }

    @Override
    public String getName() 
    {
        return "Unplaceable";
    }

    @Override
    public int getStartLevel() 
    {
        return 1;
    }
}
