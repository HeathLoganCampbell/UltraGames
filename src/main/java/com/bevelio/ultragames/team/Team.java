package com.bevelio.ultragames.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;

public class Team
{
	private String name
				 , defaultKit;
	private ChatColor prefix;
	private List<String> spawns;
	private List<UUID> members;
	
	private org.bukkit.scoreboard.Team bukkitTeam;
	
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

	public List<String> getSpawnNames()
	{
		return spawns;
	}

	public void setSpawn(String spawnName)
	{
		this.spawns.add(spawnName);
	}
	
	public void addAllSpawns(List<String> spawns)
	{
		this.spawns.addAll(spawns);
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

	public org.bukkit.scoreboard.Team getBukkitTeam() {
		return bukkitTeam;
	}

	public void setBukkitTeam(org.bukkit.scoreboard.Team bukkitTeam) {
		this.bukkitTeam = bukkitTeam;
	}
}
