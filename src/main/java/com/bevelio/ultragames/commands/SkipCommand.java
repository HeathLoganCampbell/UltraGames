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

public class SkipCommand extends Command
{

	public SkipCommand()
	{
		super("Skip", "Skip to the next game", "/skip", Arrays.asList("end"));
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
		
		if(player.hasPermission("ultragames.commands.skip"))
		{
			MatchManager mm = BevelioPlugin.getMatchManager();
			
			mm.setState(MatchState.FINISHING);
			player.sendMessage(ChatColor.GREEN + "Match ended!");
		}
		return false;
	}

}
