package com.bevelio.ultragames.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import com.bevelio.ultragames.commons.Settings;
import com.bevelio.ultragames.commons.utils.Decompress;

public class WorldManager
{	
	public File getWorld(String worldName)
	{
		for(File file : this.fetchWorlds())
		{
			if(file.getName().toLowerCase().contains(worldName.toLowerCase()))
			{
				return file;
			}
		}
		return null;
	}
	
	public File getRandomWorld()
	{
		List<File> worlds = this.fetchWorlds();
		return worlds.get(new Random().nextInt(worlds.size()));
	}
	
	public WorldData createNewWorld(int id, String worldName)
	{
		File selectedWorld = null;
		if(worldName != null)
		{
			selectedWorld = this.getWorld(worldName);
		}
		
		if(selectedWorld == null)
		{
			selectedWorld = this.getRandomWorld();
		}
		
		String name = Settings.GAME_WORLD_NAME + id;
		File worldFile = new File(name);
		
		if(Bukkit.getWorld(name) != null) {
			World lobby = Bukkit.getWorld(Settings.LOBBY_WORLD);
			if(lobby == null)
			{
				Bukkit.broadcastMessage("Welp, We fucked up! the lobby world couldn't be found!!!");
				return null;
			}
			Location spawn = lobby.getSpawnLocation().add(0.5, 0.5, 0.5);
			Bukkit.getOnlinePlayers().forEach(player -> player.teleport(spawn));
			Bukkit.unloadWorld(name, false);
		}
		
		if(worldFile.exists())
		{
			try 
			{
				FileUtils.deleteDirectory(new File(name));
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
		try 
		{
			new Decompress(selectedWorld.getAbsolutePath(), worldFile.getAbsolutePath()).unzipSpeed();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		World world = Bukkit.createWorld(new WorldCreator(name)
				.generator(new ChunkGenerator() 
				{
					@Override
					public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes)
					{
						return new byte[world.getMaxHeight() >> 4][];
					}
				}
			));
		
		world.setAutoSave(false);
		
		WorldData worldData = new WorldData(world);
		try
		{
			worldData.load();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return worldData;
	}
	
	public WorldData createNewWorld(int id)
	{
		return this.createNewWorld(id, null);
	}
	
	public void deleteMatch(int id)
	{
		String name = Settings.GAME_WORLD_NAME + id;
		World world = Bukkit.getWorld(name);
		if(world != null)
		{
			for(Entity en : world.getEntities())
			{
				if(!(en instanceof Player))
				{
					en.remove();
				}
			}
		}
		Bukkit.unloadWorld(name, false);
		File worldFile = new File(name);
		if(worldFile.exists())
		{
			try 
			{
				FileUtils.deleteDirectory(worldFile);
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	public int getMatchWorldsSize()
	{
		return this.fetchWorlds().size();
	}
	
	private List<File> fetchWorlds() 
	{
		return Arrays.asList(new File(Settings.PATH_TO_MAPS).listFiles()).stream().filter(file -> file.getAbsolutePath().contains(".zip")).collect(Collectors.toList());
	}
}
