package com.bevelio.bevelio.commons.updater;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Updater {

	public Updater(JavaPlugin plugin) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			UpdateType[] types = UpdateType.values();
			
			for(int i = 0; i < types.length; i++)
				if(types[i].elapsed()) Bukkit.getPluginManager().callEvent(new UpdateEvent(types[i]));
		}, 0l, 1l);
	}
}
