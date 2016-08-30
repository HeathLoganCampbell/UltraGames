package com.bevelio.ultragames.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.bevelio.ultragames.commons.enchantments.EnchantmentManager;
import com.bevelio.ultragames.core.Match;
import com.bevelio.ultragames.core.Objective;
import com.bevelio.ultragames.core.Spawn;
import com.bevelio.ultragames.kit.ArmorType;
import com.bevelio.ultragames.kit.Kit;
import com.bevelio.ultragames.map.WorldData;
import com.bevelio.ultragames.team.Team;

public class ConfigManager
{
	public ConfigManager()
	{
		
	}
	
	public void loadMap(ConfigurationSection config, WorldData worlddata)
	{
		loadMapInfo(config, worlddata);
		worlddata.spawns = loadSpawns(config);
		worlddata.teams = loadTeams(config);
		worlddata.objectives = loadObjectives(config);
	}
	
	public void loadMapInfo(ConfigurationSection config, WorldData worlddata)
	{
		worlddata.displayName = config.getString("Name");
		worlddata.gameType = config.getString("GameType");
		worlddata.version = config.getString("Version");
		worlddata.authors = config.getStringList("Authors").toString();
		worlddata.itemRemoves = this.parseMaterials(config);
	}
	
	public HashSet<Material> parseMaterials(ConfigurationSection config)
	{
		HashSet<Material> materials = new HashSet<>();
		if(config.contains("RemoveItems"))
		{
			for (String removeItems : config.getStringList("RemoveItems")) 
			{
				Material material = Material.getMaterial(removeItems.toUpperCase());
				if(material == null)
				{
					System.out.println("Failed to find '" + removeItems + "' as a material in removeItems");
					continue;
				}
				materials.add(material);
		    }
		}
		return materials;
	}
	
	public List<Spawn> loadSpawns(ConfigurationSection config)
	{
		List<Spawn> spawns = new ArrayList<>();
		
		for (String spawnName : config.getConfigurationSection("Spawns").getKeys(false)) 
		{
			Spawn spawn = this.parseSpawn(config.getConfigurationSection("Spawns." + spawnName));
	        spawns.add(spawn);
	    }
		
		return spawns;
	}
	
	public List<Team> loadTeams(ConfigurationSection config)
	{
		List<Team> teams = new ArrayList<>();
		
		for (String teamName : config.getConfigurationSection("Teams").getKeys(false)) 
		{
			Team team = this.parseTeam(config.getConfigurationSection("Teams." + teamName));
			teams.add(team);
	    }
		
		return teams;
	}
	
	public List<Objective> loadObjectives(ConfigurationSection config)
	{
		List<Objective> objectives = new ArrayList<>();
		
		for (String objectiveName : config.getConfigurationSection("Objectives").getKeys(false)) 
		{
			Objective objective = this.parseObjective(config.getConfigurationSection("Objectives." + objectiveName));
			objectives.add(objective);
	    }
		
		return objectives;
	}
	
	public List<Kit> loadKits(ConfigurationSection config)
	{
		List<Kit> kits = new ArrayList<>();
		if(config != null)
		{
			if(config.contains("Kits"))
			{
				for (String kitName : config.getConfigurationSection("Kits").getKeys(false)) {
			        Kit kit = parseKit(config.getConfigurationSection("Kits." + kitName));
			        kits.add(kit);
			    }
			}
		}
		return kits;
	}
	
	public boolean isNumeric(String str) 
	{
        try 
        {
            Integer.parseInt(str);
        } 
        catch (NumberFormatException nfe) 
        {
            return false;
        }
        return true;
    }
	
	public int getNumericValue(String str) 
	{
        try 
        {
           return Integer.parseInt(str);
        } 
        catch (NumberFormatException nfe) 
        {
           
        }
        return 0;
    }
	
	 public ItemStack[] parseItem(String string) {
	        if (string == null)
	            return new ItemStack[] { null };
	        String[] args = string.split(" ");
	        try {
	            double amount = Integer.parseInt(args[2]);
	            ItemStack[] items = new ItemStack[(int) Math.ceil(amount / 64)];
	            if (items.length <= 0)
	                return new ItemStack[] { null };
	            for (int i = 0; i < items.length; i++) {
	                int id = isNumeric(args[0]) ? Integer.parseInt(args[0])
	                        : (Material.getMaterial(args[0].toUpperCase()) == null ? Material.AIR : Material.getMaterial(args[0]
	                                .toUpperCase())).getId();
	                if (id == 0) {
	                    System.out.print("Unknown item " + args[0] + "!");
	                    return new ItemStack[] { null };
	                }
	                ItemStack item = new ItemStack(id, (int) amount, (short) Integer.parseInt(args[1]));
	                String[] newArgs = Arrays.copyOfRange(args, 3, args.length);
	                for (String argString : newArgs) {
	                    if (argString.contains("Name=")) {
	                        String name = ChatColor.translateAlternateColorCodes('&', argString.substring(5)).replaceAll("_", " ");
	                        if (ChatColor.getLastColors(name).equals(""))
	                            name = ChatColor.WHITE + name;
	                        ItemMeta meta = item.getItemMeta();
	                        String previous = meta.getDisplayName();
	                        if (previous == null)
	                            previous = "";
	                        meta.setDisplayName(name + previous);
	                        item.setItemMeta(meta);
	                    } else if (argString.contains("Color=") && item.getType().name().contains("LEATHER")) {
	                        String[] name = argString.substring(6).split(":");
	                        int[] ids = new int[3];
	                        for (int o = 0; o < 3; o++)
	                            ids[o] = Integer.parseInt(name[o]);
	                        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
	                        meta.setColor(Color.fromRGB(ids[0], ids[1], ids[2]));
	                        item.setItemMeta(meta);
	                    } else if (argString.equalsIgnoreCase("UniqueItem")) {
	                        ItemMeta meta = item.getItemMeta();
	                        String previous = meta.getDisplayName();
	                        if (previous == null)
	                            previous = "";
	                        meta.setDisplayName(previous + "UniqueIdentifier");
	                        item.setItemMeta(meta);
	                    }
	                    if (argString.contains("Lore=")) {
	                        String name = ChatColor.translateAlternateColorCodes('&', argString.substring(5)).replaceAll("_", " ");
	                        ItemMeta meta = item.getItemMeta();
	                        List<String> lore = meta.getLore();
	                        if (lore == null)
	                        {
	                        	lore = new ArrayList<String>();
	                        }
	                        for (String a : name.split("\\n"))
	                        {
	                            lore.add(a);
	                        }
	                        meta.setLore(lore);
	                        item.setItemMeta(meta);
	                    }
	                }
	                
	                for (int n = 0; n < newArgs.length; n++)
	                {
	                    Enchantment ench = Enchantment.getByName(newArgs[n]);
	                    if (ench == null)
	                    {
	                        ench = Enchantment.getByName(newArgs[n].replace("_", " "));
	                    }
	                    if (ench == null)
	                    {
	                        ench = Enchantment.getByName(newArgs[n].replace("_", " ").toUpperCase());
	                    }
	                    if (ench == null)
	                    {
	                        continue;
	                    }
	                    System.out.println("New Ench added to an item " + ench.getName());
	                    item.addUnsafeEnchantment(ench, Integer.parseInt(newArgs[n + 1]));
	                    n++;
	                }
	                item = EnchantmentManager.updateEnchants(item);
	                amount = amount - 64;
	                items[i] = item;
	            }
	            return items;
	        } catch (Exception ex) {
	            String message = ex.getMessage();
	            if (ex instanceof ArrayIndexOutOfBoundsException)
	                message = "java.lang.ArrayIndexOutOfBoundsException: " + message;
	            System.out.print("Not allowed " + string  + " " + message);
	            ex.printStackTrace();
	        }
	        return new ItemStack[] { null };
	    }
	 
	public PotionEffect parseEffect(String potionStr)
	{
		if(potionStr == null)
		{
			return null;
		}
		String[] args = potionStr.split(" ");
		
		try 
		{
			PotionEffectType potType = isNumeric(args[0]) ? PotionEffectType.getById(Integer.parseInt(args[0])) : PotionEffectType.getByName(args[0].toUpperCase());
			int seconds = Integer.parseInt(args[1]) * 20;
			int lvl = Integer.parseInt(args[2]);
			boolean hideEffect = false;
			if(args.length >= 4)
			{
				hideEffect = Boolean.getBoolean(args[3]);
			}
			return new PotionEffect(potType, seconds, lvl, hideEffect);
		}  
		catch (Exception ex)
		{
			String message = ex.getMessage();
	      	if (ex instanceof ArrayIndexOutOfBoundsException)
	      	{
	      		message = "java.lang.ArrayIndexOutOfBoundsException: " + message;
	      		System.out.print("Not allowed " + potionStr  + " " + message);
	      	}
	            ex.printStackTrace();
	            
		}
		return null;
	}
	
	public Kit parseKit(ConfigurationSection path)
	{
		
		String desc = ChatColor.translateAlternateColorCodes('&', path.getString("Description"));
        String name = path.getString("Name");
        if (name == null)
        {
            name = path.getName();
        }
        name = ChatColor.translateAlternateColorCodes('&', name);
        Kit kit = new Kit(name);
        kit.setArmor(ArmorType.HELMET, parseItem(path.getString("Helmet"))[0]);
        kit.setArmor(ArmorType.CHESTPLATE, parseItem(path.getString("Chestplate"))[0]);
        kit.setArmor(ArmorType.LEGGINGS, parseItem(path.getString("Leggings"))[0]);
        kit.setArmor(ArmorType.BOOTS, parseItem(path.getString("Boots"))[0]);

        if(path.contains("Items"))
        {
	        for(int i = 0; i < 32; i++)
	        {
	        	String slottedItem = path.getString("Items."+ i);
	        	if(path.contains("Items."+ i))
	        	{
	        		kit.addItem(i, this.parseItem(slottedItem)[0]);
	        	}
	        }
        }
        
        if(path.contains("Potions"))
        {
	        for(String potStr : path.getStringList("Potions"))
	        {
	        	kit.addEffect(this.parseEffect(potStr));
	        }
        }
       
        
		return kit;
	}
	
	public Location parseLocation(ConfigurationSection path)
	{
		if(path == null)
		{
			return null;
		}
		
		double x = 0;
		double y = 0;
		double z = 0;
		float yaw = 0;
		float pitch = 0;
		
		if(path.contains("X"))
		{
			x = path.getDouble("X");
		}
		
		if(path.contains("x"))
		{
			x = path.getDouble("x");
		}
		
		if(path.contains("Y"))
		{
			y = path.getDouble("Y");
		}
		
		if(path.contains("y"))
		{
			y = path.getDouble("y");
		}
		
		if(path.contains("Z"))
		{
			z = path.getDouble("Z");
		}
		
		if(path.contains("z"))
		{
			z = path.getDouble("z");
		}

		if(path.contains("yaw"))
		{
			yaw = path.getInt("yaw");
		}
		
		if(path.contains("Yaw"))
		{
			yaw = path.getInt("Yaw");
		}
		
		if(path.contains("Pitch"))
		{
			pitch = path.getInt("Pitch");
		}
		
		if(path.contains("pitch"))
		{
			pitch = path.getInt("pitch");
		}
		
		return new Location(null, x, y, z, yaw, pitch);
	}
	
	public Spawn parseSpawn(ConfigurationSection path)
	{
        String name = path.getString("Name");
        if (name == null)
        {
            name = path.getName();
        }
        name = ChatColor.translateAlternateColorCodes('&', name);
        String kit = path.getString("Kit");
        String msg = path.getString("custom-message");
        Location location = this.parseLocation(path.getConfigurationSection("Location"));
        
        Spawn spawn = new Spawn(name, location, kit); 
        if(msg == null)
        {
        	spawn.setCustomMessage(msg);
        }
        
		return spawn;
	}
	
	public Team parseTeam(ConfigurationSection path)
	{
        String name = path.getString("Name");
        if (name == null)
        {
            name = path.getName();
        }
        name = ChatColor.translateAlternateColorCodes('&', name);
        
        String color = path.getString("Color");
        ChatColor chatColor = null;
        
        try
        {
        	chatColor = ChatColor.valueOf(color.toUpperCase().replace(" ", "_"));
        }
        catch(IllegalArgumentException e)
        {
        	System.out.println("Invalid color for team '" + name + "' with the color '" + color + "'");
        	return new Team(name, ChatColor.WHITE);
        }
        
        List<String> spawnNames = path.getStringList("Spawns");
        
        Team team = new Team(name, chatColor);
        team.addAllSpawns(spawnNames);
		return team;
	}
	
	@SuppressWarnings("deprecation")
	public Objective parseObjective(ConfigurationSection path)
	{
		String name = path.getString("Name");
        if (name == null)
        {
            name = path.getName();
        }
        
		String objectiveType = path.getString("ObjectiveType");
		int radius = path.getInt("Radius");
		Material material = Material.getMaterial(path.getString("Material").toUpperCase().replace(" ", "_"));
		if(material == null)
		{
			if(this.isNumeric(path.getString("Material")))
			{
				material = Material.getMaterial(this.getNumericValue(path.getString("Material")));
			} 
			else
			{
				material = Material.OBSIDIAN;
			}
		}
		Location location = this.parseLocation(path.getConfigurationSection("Location"));
		String team = path.getString("Team");
		boolean gen = true;
		if(path.contains("Generate"))
		{
			gen = Boolean.getBoolean(path.getString("Generate"));
		}
		
		Objective objective = new Objective();
		objective.name = name;
		objective.radius = radius;
		objective.material = material;
		objective.location = location;
		objective.objectiveType = objectiveType;
		objective.teamName = team;
		objective.active = true;
		objective.generate = gen;
		return objective;
	}
}
