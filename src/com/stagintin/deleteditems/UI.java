package com.stagintin.deleteditems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class UI {
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_slots = 6 * 9; // Rows * Columns

	public static void initialize() {
		inventory_name = Utils.chat("&6Test GUI");
		inv = Bukkit.createInventory(null, inv_slots);
	}

	public static Inventory GUI(Player p) {
		Inventory toReturn = Bukkit.createInventory(null, inv_slots, inventory_name);

		Utils.createItem(inv, Material.TNT.toString(), 1, 1, "&cTest Item", "&7This is lore line 1", "&bSecond Line!");

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		if (Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equalsIgnoreCase(Utils.chat("&cTest Item"))) {
			p.setDisplayName(Utils.chat("&8[&6*&8]&6 You have successfully made a GUI!"));
		}
	}
}
