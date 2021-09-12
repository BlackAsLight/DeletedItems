package com.stagintin.deleteditems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopUI {
	public final static String title = Utils.chat("&3Deleted Items");
	public final static int slots = 54; // Must be a multiple of 9, and a max of 54.

	public static Inventory Shop(Player player) {
		Inventory shop = Bukkit.createInventory(null, slots, title);

		int page = 0;
		LoadPage(shop, page);
		shop.setItem(46, Utils.createItemStack("-1", Material.WHITE_CANDLE, 1));
		shop.setItem(47, Utils.createItemStack("-4", Material.LIGHT_GRAY_CANDLE, 4));
		shop.setItem(48, Utils.createItemStack("-8", Material.BLACK_CANDLE, 8));
		shop.setItem(50, Utils.createItemStack("+8", Material.BLACK_CANDLE, 8));
		shop.setItem(51, Utils.createItemStack("+4", Material.LIGHT_GRAY_CANDLE, 4));
		shop.setItem(52, Utils.createItemStack("+1", Material.WHITE_CANDLE, 1));

		return shop;
	}

	public static void Click(Inventory shop, Player player, ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta != null) {
			String displayName = itemMeta.getDisplayName().toLowerCase();
			switch (displayName) {
				case "previous" -> PreviousPage(shop, player, itemMeta);
				case "-1" -> NegOne(shop, player);
				case "-4" -> NegFour(shop, player);
				case "-8" -> NegEight(shop, player);
				case "+8" -> PlusEight(shop, player);
				case "+4" -> PlusFour(shop, player);
				case "+1" -> PlusOne(shop, player);
				case "next" -> NextPage(shop, player, itemMeta);
				default -> BuyItem(player, itemStack);
			}
		}
	}

	private static void PreviousPage(Inventory shop, Player player, ItemMeta meta) {
		int page = Integer.parseInt(meta.getLore().get(0).split(" ")[1]) - 1;
		if (page >= 0)
			LoadPage(shop, page);
		else
			player.sendMessage("No More Pages!");
	}

	private static void NegOne(Inventory shop, Player player) {

	}

	private static void NegFour(Inventory shop, Player player) {

	}

	private static void NegEight(Inventory shop, Player player) {

	}

	private static void PlusEight(Inventory shop, Player player) {

	}

	private static void PlusFour(Inventory shop, Player player) {

	}

	private static void PlusOne(Inventory shop, Player player) {

	}

	private static void NextPage(Inventory shop, Player player, ItemMeta meta) {
		int page = Integer.parseInt(meta.getLore().get(0).split(" ")[1]) + 1;
		int items = Main.itemStacks.toArray().length;
		if (45 * page < items)
			LoadPage(shop, page);
		else
			player.sendMessage("No More Pages!");
	}

	private static void BuyItem(Player player, ItemStack itemStack) {

	}

	private static void LoadPage(Inventory shop, int page) {
		int items = Main.itemStacks.toArray().length;
		for (int i = 0; i < 45; ++i) {
			int index = 45 * page + i;
			if (index < items) {
				ItemStack itemStack = Main.itemStacks.get(index).clone();

				ItemMeta itemMeta = itemStack.getItemMeta();
				List<String> subLines = new ArrayList<>();
				subLines.add("$1.95/Item");
				subLines.add("Stock: " + itemStack.getAmount());
				itemMeta.setLore(subLines);
				itemStack.setItemMeta(itemMeta);

				int maxAmount = itemStack.getMaxStackSize();
				if (maxAmount < itemStack.getAmount()) {
					itemStack.setAmount(maxAmount);
				}

				shop.setItem(i, itemStack);
			}
			else
				shop.setItem(i, null);
		}
		shop.setItem(45, Utils.createItemStack("Previous", Material.PAPER, 1, "Page: " + page));
		shop.setItem(53, Utils.createItemStack("Next", Material.PAPER, 1, "Page: " + page));
	}
}
