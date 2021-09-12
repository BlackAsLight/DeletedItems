package com.stagintin.deleteditems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopUI {
	public final static String title = "Deleted Items";
	public final static int slots = 54; // Must be a multiple of 9, and a max of 54.

	public static Inventory Shop(Player player) {
		Inventory shop = Bukkit.createInventory(null, slots, title);

		int page = 0;
		LoadPage(shop, page);
		shop.setItem(46, Utils.createItemStack("-1", Material.WHITE_CANDLE, 1));
		shop.setItem(47, Utils.createItemStack("-4", Material.LIGHT_GRAY_CANDLE, 4));
		shop.setItem(48, Utils.createItemStack("-8", Material.BLACK_CANDLE, 8));
		shop.setItem(49, Utils.createItemStack("Page Settings", Material.BOOK, 1));
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
				case "first" -> LoadPage(shop, 0);
				case "previous" -> PreviousPage(shop, itemMeta);
				case "-1" -> UpdatePage(shop, -1);
				case "-4" -> UpdatePage(shop, -4);
				case "-8" -> UpdatePage(shop, -8);
				case "page settings" -> SettingsPage(shop);
				case "+8" -> UpdatePage(shop, 8);
				case "+4" -> UpdatePage(shop, 4);
				case "+1" -> UpdatePage(shop, 1);
				case "next" -> NextPage(shop, itemMeta);
				case "last" -> LoadPage(shop, (int) Math.ceil((Main.getItemStacks().toArray().length - 1) / 45));
				default -> BuyItem(shop, player, itemStack);
			}
		}
	}

	private static void PreviousPage(Inventory shop, ItemMeta meta) {
		int page = Integer.parseInt(meta.getLore().get(0).split(" ")[1]) - 1;
		if (page >= 0)
			LoadPage(shop, page);
	}

	private static void SettingsPage(Inventory shop) {
		ItemStack itemStack = shop.getItem(49);
		int amount = itemStack.getAmount();
		if (amount == 1) {
			shop.setItem(45, UpdateDisplayName(shop.getItem(45), "First"));
			shop.setItem(53, UpdateDisplayName(shop.getItem(53), "Last"));
		}
		else {
			shop.setItem(45, UpdateDisplayName(shop.getItem(45), "Previous"));
			shop.setItem(53, UpdateDisplayName(shop.getItem(53), "Next"));
		}
		itemStack.setAmount(amount % 2 + 1);
		shop.setItem(49, itemStack);
	}

	private static void NextPage(Inventory shop, ItemMeta meta) {
		int page = Integer.parseInt(meta.getLore().get(0).split(" ")[1]) + 1;
		int items = Main.getItemStacks().toArray().length;
		if (45 * page < items)
			LoadPage(shop, page);
	}

	private static void BuyItem(Inventory shop, Player player, ItemStack itemStack) {
		PlayerInventory inventory = player.getInventory();
		ItemStack[] slots = inventory.getContents();
		int slot = -1;
		for (int i = 0; i < 36; ++i) {
			if (slots[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			player.sendMessage("Your Inventory is Full!");
		}
		else {
			ItemMeta itemMeta = itemStack.getItemMeta();
			double price = Double.parseDouble(itemMeta.getLore().get(0).split("/")[0].split("\\$")[1]);

			double balance = Main.econ.getBalance(player);
			int amount = itemStack.getAmount();
			if (amount * price > balance)
				amount = (int) Math.floor(balance / price);
			if (amount > 0) {
				itemMeta.setLore(null);
				itemStack.setItemMeta(itemMeta);
				itemStack.setAmount(amount);
				amount = Main.subtractItem(itemStack);
				Main.econ.withdrawPlayer(player, amount * price);
				itemStack.setAmount(amount);
				inventory.setItem(slot, itemStack);
				player.sendMessage(itemStack.getType() + ": $" + amount * price);
				LoadPage(shop, Math.min(Integer.parseInt(shop.getItem(53).getItemMeta().getLore().get(0).split(" ")[1]), (int) Math.ceil((Main.getItemStacks().toArray().length - 1) / 45)));
			}
			else
				player.sendMessage("You don't have enough Money to purchase that.");
		}
	}

	private static void LoadPage(Inventory shop, int page) {
		int items = Main.getItemStacks().toArray().length;
		for (int i = 0; i < 45; ++i) {
			int index = 45 * page + i;
			if (index < items) {
				ItemStack itemStack = Main.getItemStacks().get(index).clone();

				ItemMeta itemMeta = itemStack.getItemMeta();
				List<String> subLines = new ArrayList<>();
				subLines.add("$" + Main.calPrice(itemStack) + "/Item");
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
		ItemStack left = shop.getItem(45);
		ItemStack right = shop.getItem(53);
		shop.setItem(45, Utils.createItemStack(left == null ? "Previous" : left.getItemMeta().getDisplayName(), Material.PAPER, 1, "Page: " + page));
		shop.setItem(53, Utils.createItemStack(right == null ? "Next" : right.getItemMeta().getDisplayName(), Material.PAPER, 1, "Page: " + page));
	}

	private static ItemStack UpdateDisplayName(ItemStack itemStack, String displayName) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	private static void UpdatePage(Inventory shop, int amount) {
		ItemStack[] itemStacks = shop.getContents();
		for (int i = 0; i < 45; ++i) {
			ItemStack itemStack = itemStacks[i];
			if (itemStack != null) {
				itemStack.setAmount(Math.max(1, Math.min(amount + itemStack.getAmount(), Math.min(itemStack.getMaxStackSize(), Integer.parseInt(itemStack.getItemMeta().getLore().get(1).split(" ")[1])))));
				shop.setItem(i, itemStack);
			}
		}
	}
}
