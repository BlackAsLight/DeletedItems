package com.stagintin.deleteditems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	// Make Text Colourful.
	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	// Create a new ItemStack.
	public static ItemStack createItemStack(Material id, int amount) {
		return new ItemStack(id, amount);
	}

	// Create a new ItemStack with provided Display Name and Lore.
	public static ItemStack createItemStack(String displayName, Material id, int amount, String... subLines) {
		// Create itemStack.
		ItemStack itemStack = createItemStack(id, amount);

		// Add Display Name.
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (displayName != null)
			itemMeta.setDisplayName(displayName);

		// Add Lore.
		if (subLines.length > 0) {
			List<String> lines = new ArrayList<>();
			for (String line : subLines)
				lines.add(chat(line));
			itemMeta.setLore(lines);
		}

		// Return itemStack.
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
}
