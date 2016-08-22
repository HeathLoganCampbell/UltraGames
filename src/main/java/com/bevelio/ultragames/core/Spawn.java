package com.bevelio.ultragames.core;

import org.bukkit.Location;

public class Spawn
{
	private String name;
	private Location spawn;
	private String defaultKit
				 , customMsg;
	
	public Spawn(String name, Location spawn, String defaultKit)
	{
		this.name = name;
		this.spawn = spawn;
		this.defaultKit = defaultKit;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Location getSpawn()
	{
		return spawn;
	}

	public void setSpawn(Location spawn)
	{
		this.spawn = spawn;
	}

	public String getDefaultKit()
	{
		return defaultKit;
	}

	public void setDefaultKit(String defaultKit)
	{
		this.defaultKit = defaultKit;
	}

	public String getCustomMessage() {
		return customMsg;
	}

	public void setCustomMessage(String customMsg) {
		this.customMsg = customMsg;
	}
}
