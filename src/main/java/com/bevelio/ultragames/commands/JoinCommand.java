package com.bevelio.ultragames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bevelio.ultragames.core.MatchState;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class JoinCommand extends Command
{

	public JoinCommand()
	{
		super("join");
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
		if(BevelioPlugin.getMatchManager().getState() != MatchState.LIVE)
		{
			sender.sendMessage(ChatColor.RED + "Game has not started.");
			return false;
		}
		
		if(BevelioPlugin.getMatchManager().isPlaying(player))
		{
			sender.sendMessage(ChatColor.RED + "You are already on " + BevelioPlugin.getMatchManager().getMatch().getTeam(player).getDisplayName() + ChatColor.RED +" team");
			return false;
		}
		
		Team team = BevelioPlugin.getMatchManager().joinMatch(player);
		player.sendMessage(ChatColor.GREEN + "You have joinned " + team.getDisplayName() + ChatColor.GREEN + "!");
		return false;
	}

}
