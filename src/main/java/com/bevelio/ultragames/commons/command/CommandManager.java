package com.bevelio.ultragames.commons.command;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

/**
 * The Command Manager is a hack of a class
 * it uses reflection to access bukkits default 
 * simple command map so you can add commands without
 * the aid of a plugin.yml. This is also friendly with
 * other plugins unlike override the simple command map.
 * </br>
 * </br>
 * <b>
 * Add a new command by extending {@link org.bukkit.command.Command} 
 * then calling {@link #registerCommand(String)}.
 * </b>
 * </br>
 * </br>
 * Example found at {@link com.mcsabotage.heart.command.commands.PingCommand}.
 * </br>
 * @author Sprockbot
 */
public class CommandManager {
	private CommandMap commandMap;

	public CommandManager() {

		try {
			Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			
			bukkitCommandMap.setAccessible(true);
			commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void registerCommand(Command command) {
		commandMap.register(command.getName(), command);
	}
}

