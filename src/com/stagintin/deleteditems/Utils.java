package com.stagintin.deleteditems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static ItemStack createItemStack(Material id, int amount) {
		return new ItemStack(id, amount);
	}

	public static ItemStack createItemStack(String displayName, Material id, int amount, String... subLines) {
		ItemStack itemStack = createItemStack(id, amount);
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (displayName != null)
			itemMeta.setDisplayName(displayName);
		if (subLines.length > 0) {
			List<String> lines = new ArrayList<>();
			for (String line : subLines)
				lines.add(chat(line));
			itemMeta.setLore(lines);
		}
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
}
