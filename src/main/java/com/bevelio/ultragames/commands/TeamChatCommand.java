package com.bevelio.ultragames.commands;

import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bevelio.ultragames.core.MatchState;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class TeamChatCommand extends Command
{

	public TeamChatCommand()
	{
		super("teamchat", "Message your team" , "/tc <message>",Arrays.asList("tc","teammessage", "tm"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only players can do this command.");
			return false;
		}
		if(args.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "The message can't just be nothing!");
			return true;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		
		if(!BevelioPlugin.getMatchManager().isPlaying(player))
		{
			player.sendMessage(ChatColor.BOLD + "You aren't even playing!");
			return true;
		}
		
		String message = StringUtils.join(args, " ");
		String fomate = ChatColor.GRAY + "[TeamChat " + player.getName() + "> " + message;
		
		Team team = BevelioPlugin.getMatchManager().getMatch().getTeam(player);
		for(Player viewer : Bukkit.getOnlinePlayers())
		{
			if(team.isMember(viewer.getUniqueId()) || viewer.hasPermission("ultragames.command.tc"))
			{
				viewer.sendMessage(fomate);
			} 
		}
		return false;
	}

}
