package com.stagintin.deleteditems.events;

import com.stagintin.deleteditems.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Damage implements Listener {
	private Main plugin;
	public Damage(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() == EntityType.DROPPED_ITEM) {
			Item item = (Item) entity;
			if (item.getLastDamageCause() == null) {
				ItemStack itemStack = item.getItemStack();
				Main.addItem(itemStack);
				plugin.getLogger().info(Integer.toString(itemStack.getAmount()) + ' ' + itemStack.getType().name() + " got deleted by " + event.getCause().name() + '.');
			}
		}
	}
}
