package com.stagintin.deleteditems.events;

import com.stagintin.deleteditems.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Despawn implements Listener {
	private Main plugin;
	public Despawn(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDespawn(ItemDespawnEvent event) {
		ItemStack itemStack = event.getEntity().getItemStack();
		Main.addItem(itemStack);
		plugin.getLogger().info(Integer.toString(itemStack.getAmount()) + ' ' + itemStack.getType().name() + " despawned.");
	}
}
