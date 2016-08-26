package com.bevelio.ultragames.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bevelio.ultragames.core.MatchState;
import com.bevelio.ultragames.plugin.BevelioPlugin;
import com.bevelio.ultragames.team.Team;

public class SpectatorCommand extends Command
{

	public SpectatorCommand()
	{
		super("spectator", "Go back into spectator mode", "/spectator", Arrays.asList("leave", "quit"));
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
			BevelioPlugin.getMatchManager().leaveGame(player);
			BevelioPlugin.getMatchManager().addSpectator(player);
			player.sendMessage(ChatColor.BOLD + "You have left the game");
			return false;
		}
		return false;
	}

}
