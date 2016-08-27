package com.bevelio.ultragames.commands;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bevelio.ultragames.core.MatchManager;
import com.bevelio.ultragames.core.MatchState;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class NextMatchCommand extends Command
{

	public NextMatchCommand()
	{
		super("nextMatch", "Skip to the next game", "/nextMatch <WorldName>", Arrays.asList("nextgame"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only players can do this command.");
			return false;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		
		if(player.hasPermission("ultragames.commands.nextmatch"))
		{
			if(args.length == 1)
			{
				String worldName = args[0];
				MatchManager mm = BevelioPlugin.getMatchManager();
				if(mm.isValidWorld(worldName))
				{
					mm.setNextMatch(mm.createMatch(worldName));
					player.sendMessage(ChatColor.GREEN + "Next map will be '" + worldName + "'.");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Unknown Map by the name of '" + worldName + "'!");
				}
			}
			else
			{
				player.sendMessage(this.getUsage());
			}
		}
		return false;
	}

}
