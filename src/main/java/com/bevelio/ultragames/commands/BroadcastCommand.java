package com.bevelio.ultragames.commands;

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

public class BroadcastCommand extends Command
{

	public BroadcastCommand()
	{
		super("broadcast");
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
		if(!player.hasPermission("ultragame.command.broadcast"))
		{
			return false;
		}
		
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, " ")));
		return false;
	}

}
