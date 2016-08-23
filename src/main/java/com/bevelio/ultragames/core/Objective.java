package com.bevelio.ultragames.core;

import org.bukkit.Location;
import org.bukkit.Material;

import com.bevelio.ultragames.commons.utils.WorldUtils;

public class Objective 
{
	public String 	name
				, 	objectiveType
				, 	teamName;
	public int 		radius;
	public Material material;
	public Location location;
	public boolean active
				 , generate;
	
	public boolean isWithin(Location location)
	{
		if(this.location == null
				|| location == null)
		{
			return false;
		}
		return WorldUtils.quickDistance(location, this.location) <= this.radius;
	}
	
	public boolean isBlock(Material material)
	{
		if(this.material == material)
		{
			return true;
		}
		return false;
	}
}
