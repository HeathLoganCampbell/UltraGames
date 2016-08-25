# UltraGames
An arcadaic type minigame plugin for spigot and bukkit, Arcade, Cycling, minecraft, minigame, minigames like Mineplex, Overcast and Hypixel
##Current Complete Games
1. Destroy The Core

##Current Planned Games
1. Team Death Match
2. Capture The Wool

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
