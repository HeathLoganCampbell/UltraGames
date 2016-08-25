package com.bevelio.ultragames.commons.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldUtils
{
	/**
	 * Turns String into a location with the formate of x%y%z%yaw%pitch
	 * @param world
	 * @param locationStr
	 * @return
	 */
	public static Location getLocation(World world, String locationStr) 
	{
		String[] cords = locationStr.split("%");
		
		double x = Double.parseDouble(cords[0]);
		double y = Double.parseDouble(cords[1]);
		double z = Double.parseDouble(cords[2]);
		
		Location location = new Location(world, x, y, z);
		
		if(cords.length > 3)
			location = new Location(world, x, y, z, Float.parseFloat(cords[3]), Float.parseFloat(cords[4]));
			
		return location;
	}
	
	public static double quickDistance(Location a, Location b)
	{
		System.out.println("Distance: " + Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY())));
		return Math.max(Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY())), Math.abs(a.getZ() - b.getZ()));
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
}
