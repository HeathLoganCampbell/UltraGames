package com.bevelio.bevelio.games.ctw;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.bevelio.bevelio.team.Team;

public class CaptureWoolPoint
{
	private String name;
	private DyeColor color;
	private Block block;
	private Team team;
	
	public CaptureWoolPoint(String name, DyeColor color, Block block, Team team)
	{
		this.name = name;
		this.color = color;
		this.block = block;
		this.team = team;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public DyeColor getColor()
	{
		return color;
	}

	public void setColor(DyeColor color)
	{
		this.color = color;
	}

	public Block getBlock()
	{
		return block;
	}

	public void setBlock(Block block)
	{
		this.block = block;
	}

	public Team getTeam()
	{
		return team;
	}

	public void setTeam(Team team)
	{
		this.team = team;
	}
	
	@SuppressWarnings({ "deprecation" })
	public boolean isMatch(Block block)
	{
		if(block == null)
		{
			return false;
		}
		
		if(block.getType() != Material.WOOL)
		{
			return false;
		}
		
		return block.getData() == this.color.getData();
	}
}
