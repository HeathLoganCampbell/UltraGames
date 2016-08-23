package com.bevelio.ultragames.commons.utils;

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
		return Math.max(Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY())), Math.abs(a.getZ() - b.getZ()));
	}
}
