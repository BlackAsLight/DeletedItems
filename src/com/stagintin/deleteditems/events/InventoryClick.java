package com.stagintin.deleteditems.events;

import com.stagintin.deleteditems.Main;
import com.stagintin.deleteditems.UI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClick implements Listener {
	private Main plugin;
	public InventoryClick(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		// If Shop Inventory is open...
		if (event.getView().getTitle().equalsIgnoreCase(UI.inventory_name)) {
			Inventory inventory = event.getClickedInventory();

			// return if Player dropped item outside any Inventory.
			if (inventory == null)
				return;

			// If Player interacted with Shop Inventory...
			if (inventory.getType().getDefaultTitle().equalsIgnoreCase("chest")) {
				// Cancel Interaction.
				event.setCancelled(true);

				// return if Player clicked on an empty slot.
				if (event.getCurrentItem() == null)
					return;

				// If Player isn't holding anything...
				if (event.getCursor() != null) {
					// Then Player is trying to purchase an item.
					plugin.getLogger().info("User is trying to buy from shop!");
				}
			}
			// cancel if Player tries shifting from Player Inventory to Shop Inventory.
			else if (event.getClick().isShiftClick())
				event.setCancelled(true);
		}
	}
}
