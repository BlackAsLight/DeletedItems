package com.stagintin.deleteditems.events;

import com.stagintin.deleteditems.Main;
import com.stagintin.deleteditems.ShopUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
		if (event.getView().getTitle().equalsIgnoreCase(ShopUI.title)) {
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
					// Then Player made a legitimate click on an Item.
					ShopUI.Click(inventory, (Player) event.getWhoClicked(), event.getCurrentItem());
				}
			}
			// cancel if Player tries shifting from Player Inventory to Shop Inventory.
			else if (event.getClick().isShiftClick())
				event.setCancelled(true);
		}
	}
}
