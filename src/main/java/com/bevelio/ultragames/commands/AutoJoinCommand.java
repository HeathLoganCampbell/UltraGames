package com.bevelio.ultragames.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bevelio.ultragames.core.MatchState;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class AutoJoinCommand extends Command
{

	public AutoJoinCommand()
	{
		super("autojoin");
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
		
		if(BevelioPlugin.getMatchManager().isAutoJoin(uuid))
		{
			BevelioPlugin.getMatchManager().leaveAutoJoin(uuid);
			player.sendMessage(ChatColor.BOLD + "You have left auto join!");
		}
		else
		{
			BevelioPlugin.getMatchManager().addAutoJoin(uuid);
			player.sendMessage(ChatColor.BOLD + "You have joinned auto join!");
		}
		return false;
	}

}
