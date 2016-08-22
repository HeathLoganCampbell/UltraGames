package com.bevelio.ultragames.perks;

import org.bukkit.event.Listener;

public class Perk implements Listener
{
	private String name;
	private String description;
	
	public Perk(String name, String description) 
	{
		this.name = name;
		this.description = description;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public String getDescription() 
	{
		return description;
	}
}
