package com.bevelio.ultragames.games.ctw;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.team.Team;


/**
 * Capture the Wool Minigame
 * 
 * @author Bevelio
 */
public class CTW extends Match
{
	private List<CaptureWoolPoint> woolPoints;
	
	public CTW()
	{
		super("CTW", new String[] {});
		
		this.woolPoints = new ArrayList<>();
	}

	@Override
	public void onStart()
	{
		
	}
	
	public DyeColor getColor(int i)
	{
		switch(i)
		{
			case 0:
				return DyeColor.WHITE;
			case 1:
				return DyeColor.BLACK;
			case 2:
				return DyeColor.GRAY;
			case 3:
				return DyeColor.LIME;
		}
		return DyeColor.PINK;
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) 
	{
		Block block = e.getBlock();
		Block placed = e.getBlockPlaced();
		
		for(CaptureWoolPoint point : this.woolPoints)
		{
			if(point.getBlock().getX() == placed.getX()
					&& point.getBlock().getY() == placed.getY()
					&& point.getBlock().getZ() == placed.getZ())
			{
				if(point.isMatch(placed))
				{
					Bukkit.broadcastMessage("Matched");
				}
			}
		}
	}
}