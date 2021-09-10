package com.stagintin.deleteditems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static void createItem(Inventory inv, String materialId, int amount, int invSlot, String displayName, String... loreString) {
		ItemStack item;
		List<String> lore = new ArrayList<>();

		item = new ItemStack(Material.getMaterial(materialId), amount);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.chat(displayName));
		for (String s : loreString) {
			lore.add(Utils.chat(s));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);

		inv.setItem(invSlot -1, item);
	}
}
