package com.stagintin.deleteditems.events;

import com.stagintin.deleteditems.Main;
import com.stagintin.deleteditems.UI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryDrag implements Listener {
	public InventoryDrag(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		// If Shop Inventory is open...
		if (event.getView().getTitle().equalsIgnoreCase(UI.inventory_name)) {
			// If one of the slots Dragged belonged to the Shop...
			for (int slot : event.getRawSlots()) {
				if (slot < UI.inv_slots) {
					// Cancel that Drag.
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
