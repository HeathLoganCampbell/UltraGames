package com.bevelio.bevelio.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;

import com.bevelio.bevelio.core.Spawn;

public class Team
{
	private String name
				 , defaultKit;
	private ChatColor prefix;
	private List<Spawn> spawns;
	private List<UUID> members;
	
	public Team(String name, ChatColor prefix)
	{
		this.name = name;
		this.prefix = prefix;
		this.spawns = new ArrayList<>();
		this.members = new ArrayList<>();
		this.defaultKit = null;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ChatColor getPrefix()
	{
		return prefix;
	}

	public void setPrefix(ChatColor prefix)
	{
		this.prefix = prefix;
	}
	
	public void addMember(UUID uuid)
	{
		this.members.add(uuid);
	}
	
	public boolean isMember(UUID uuid)
	{
		return this.members.contains(uuid);
	}
	
	public void removeMember(UUID uuid)
	{
		this.members.remove(uuid);
	}
	
	public int size()
	{
		return this.members.size();
	}

	public List<Spawn> getSpawn()
	{
		return spawns;
	}

	public void setSpawn(Spawn spawn)
	{
		this.spawns.add(spawn);
	}

	public String getDisplayName()
	{
		return this.getPrefix() + this.getName();
	}

	public String getDefaultKit()
	{
		return defaultKit;
	}

	public void setDefaultKit(String defaultKit)
	{
		this.defaultKit = defaultKit;
	}
}
