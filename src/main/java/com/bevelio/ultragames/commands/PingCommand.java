package com.bevelio.ultragames.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class PingCommand extends Command
{
	public PingCommand(String name, String description, String usageMessage, List<String> aliases)
	{
		super("Ping", "Description", "/ping", Arrays.asList("pingMeh", "pingaliases"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) 
	{
		return false;
	}
}
