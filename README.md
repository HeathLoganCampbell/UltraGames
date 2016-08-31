# UltraGames
An arcadaic type minigame plugin for spigot and bukkit, Arcade, Cycling, minecraft, minigame, minigames like Mineplex, Overcast and Hypixel

This is a PGM type plugin... but a bit different

##Current Complete Games
1. Destroy The Core

##Current Planned Games
1. Team Death Match
2. Capture The Wool

##Create an Arena
After creating a world in single-player you want to get the world fold and create 2 file within it called Map.yml and Kit.yml (Note: the first letters of each file have to be capitalised)

after setting up the Map.yml and Kit.yml zip up 
 - level.dat
 - Map.yml
 - Kit.yml
 - region

examples of a fully set up worlds can be found at https://github.com/Bevelio/UltraGames/tree/master/src/main/resources
###Map.yml
```
Name: 'Bridge Wars 2.0'
GameType: 'DTC'
Authors: 
 - 'SeanMe'
 - 'Iswirda'
Version: 1.0.1
RemoveItems:
 - LEATHER_CHESTPLATE
 - LEATHER_LEGGINGS
 - LEATHER_BOOTS
 - STONE_SWORD
 - BOW
 - LOG
 - COMPASS
 - DIAMOND_PICKAXE

Spawns:
  purple-spawn:
    Name: 'purple-spawn'
    Kit: Purple-Kit
    custom-message: ''
    Location:
        x: 115
        y: 44
        z: 0
        pitch: 0
        yaw: 90
  yellow-spawn:
    Name: 'yellow-spawn'
    Kit: Yellow-Kit
    custom-message: ''
    Location:
        x: -194
        y: 44
        z: 0
        pitch: 0
        yaw: -90

Teams:
  Purple:
    Name: 'Purple'
    Color: 'DARK_PURPLE'
    Spawns:
     - 'purple-spawn'
  Yellow:
    Name: 'Yellow'
    Color: 'YELLOW'
    Spawns:
     - 'yellow-spawn'

Objectives:
   Purple-Core:
     Name: "Purple Core"
     ObjectiveType: "Core"
     Radius: 5
     Material: Obsidian
     Team: Purple
     Location:
        x: 39
        y: 78
        z: 1
        pitch: 0
        yaw: 0
   Yellow-Core:
     Name: "Yellow Core"
     ObjectiveType: "Core"
     Radius: 5
     Material: Obsidian
     Team: Yellow
     Location:
        x: -118
        y: 78
        z: 1
        pitch: 0
        yaw: 0
```

###Kit.yml
```
Kits:
  Yellow-Kit:
    Description: Yellow teams default kit
    Helmet: GOLD_ORE 0 1 Unlootable 1
    Chestplate: LEATHER_CHESTPLATE 0 1 Color=255:255:0
    Leggings: LEATHER_LEGGINGS 0 1 Color=255:255:0
    Boots: LEATHER_BOOTS 0 1 Color=255:255:0
    Items:
      0: STONE_SWORD 0 1 
      1: DIAMOND_PICKAXE 0 1
      2: BOW 0 1
      3: LOG 0 64 
      8: COMPASS 0 1 
      9: ARROW 0 32 
    Potions:
      - DAMAGE_RESISTANCE 8 2
      - SPEED 8 2
  Purple-Kit:
    Description: Purple teams default kit
    Helmet: LAPIS_ORE 0 1 Unlootable 1
    Chestplate: LEATHER_CHESTPLATE 0 1 Color=128:0:128
    Leggings: LEATHER_LEGGINGS 0 1 Color=128:0:128
    Boots: LEATHER_BOOTS 0 1 Color=128:0:128 
    Items:
      0: STONE_SWORD 0 1 
      1: DIAMOND_PICKAXE 0 1
      2: BOW 0 1 
      3: LOG 0 64
      8: COMPASS 0 1
      9: ARROW 0 32 
    Potions:
      - DAMAGE_RESISTANCE 8 2
      - SPEED 8 2
```

##Example
```Java
public class ExampleGame extends Match
{
	/*
	When a map is randomly picked it will be unzipped and exported to 'matches/match_00' then the data in the map.yml is loaded and we get the gametype... Then we create the game based on that gametype. then when the 15 seconds is up for the pregame the method within the game called 'onStart()' is called
	*/
	public ExampleGame()
	{
		super("ExampleGame", new String[] {"This is the description of the game", "..."});
	}
	
	@Override
	public void onStart() //Is called when the game starts
	{
	}
	
	//Match implements Listener
	@EventHandler
	public void onStick(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		Team team = this.getTeam(player);
		ItemStack item = e.getItem();
		
		if(team == null)
		{
			return;//Player is not in a team aka are in spectator mode
		}
		
		if(item != null)
		{
			if(item.getType() == Material.STICK)
			{
				player.sendMessage(ChatColor.GREEN + "You used the stick!! :DD");
				this.end(team);//Ends the game with the parameter of the team that won
			}
		}
	}
}
```

## Installation
1. Download the project... You should know how to do this by now.
2. Import project into eclipse.
3. Run as -> Maven Build.
4. Get the jar from the target file within the project.
5. Drop into /plugins in a minecraft bukkit/spigot 1.8 server.
6. Run the server AND EVERYTHING IS SET UP. :D
7. Run the server it again. :D

## Usage
This is a game engine for making team based games like such that were on the Overcast Network. (EG Destroy the Core, Team death match and others from other servers)
## Contributing
1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D
