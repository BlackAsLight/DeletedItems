package com.stagintin.deleteditems.events;

import com.stagintin.deleteditems.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;

public class Despawn implements Listener {
	private Main plugin;
	public Despawn(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDespawn(ItemDespawnEvent event) {
		Item item = event.getEntity();
		try {
			Main.addItem(item.getItemStack());
		}
		catch (Exception error) {
			plugin.getLogger().warning("Failed to add item, " + item.getName() + ", to database.");
			plugin.getLogger().warning(error.getMessage());
		}
	}
}
