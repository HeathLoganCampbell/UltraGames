package com.bevelio.ultragames.map;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;

import com.bevelio.ultragames.commons.utils.WorldUtils;

public class WorldData 
{
	public World world;
	public String displayName
				, authors
				, gameType;
	public Location spawn;
	public HashMap<String, ArrayList<Location>> teams, custom;
	
	public WorldData(World world)
	{
		this.world = world;
		
		this.custom = new HashMap<>();
		this.teams = new HashMap<>();
	}
	
	public void load() throws FileNotFoundException {
		String line = null;
		try {
			FileInputStream fstream = new FileInputStream(this.world.getName() + File.separator + "MapConfig.dat");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			ArrayList<Location> currentcustom = null
							  , currentteam = null;
			
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(":");
				
				if (tokens.length >= 2) {
					if (tokens[0].length() != 0) {
						if (tokens[0].equalsIgnoreCase("NAME")) this.displayName = tokens[1];
						else if (tokens[0].equalsIgnoreCase("AUTHOR")) this.authors = tokens[1];
						else if (tokens[0].equalsIgnoreCase("GAMETYPE")) this.gameType = tokens[1];
						else if (tokens[0].equalsIgnoreCase("SPAWN")) this.spawn = WorldUtils.getLocation(this.world, tokens[1]);
						else if (tokens[0].equalsIgnoreCase("TEAM_NAME")) {
							this.teams.put(tokens[1], new ArrayList<>());
							currentteam = this.teams.get(tokens[1]);
						} else if(tokens[0].equalsIgnoreCase("TEAM_LOCATION")) currentteam.add(WorldUtils.getLocation(this.world, tokens[1]));
					
						else if (tokens[0].equalsIgnoreCase("CUSTOM_NAME")) {
							this.custom.put(tokens[1], new ArrayList<>());
							currentcustom = this.custom.get(tokens[1]);
						} else if(tokens[0].equalsIgnoreCase("CUSTOM_LOCATION")) currentcustom.add(WorldUtils.getLocation(this.world, tokens[1]));
					
					}
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error on line: " + line);
		}
		
		if(this.authors == null) System.out.println("ERROR: Please give an author");
		if(this.spawn == null) System.out.println("ERROR: Please give an author");
		if(this.displayName == null) System.out.println("ERROR: Please give a map name");
	}
}
