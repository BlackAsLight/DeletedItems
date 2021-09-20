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

	// Set up the GUI for the Shop.
	public static Inventory Shop(Player player) {
		// Create an Inventory.
		Inventory shop = Bukkit.createInventory(null, slots, title);

		// Add in itemStack's items to the Inventory.
		LoadPage(shop, 0);
		// Add in the Buttons at the bottom of the Inventory.
		shop.setItem(slots - 8, Utils.createItemStack("-1", Material.WHITE_CANDLE, 1));
		shop.setItem(slots - 7, Utils.createItemStack("-4", Material.LIGHT_GRAY_CANDLE, 4));
		shop.setItem(slots - 6, Utils.createItemStack("-8", Material.BLACK_CANDLE, 8));
		shop.setItem(slots - 5, Utils.createItemStack("Page Settings", Material.BOOK, 1));
		shop.setItem(slots - 4, Utils.createItemStack("+8", Material.BLACK_CANDLE, 8));
		shop.setItem(slots - 3, Utils.createItemStack("+4", Material.LIGHT_GRAY_CANDLE, 4));
		shop.setItem(slots - 2, Utils.createItemStack("+1", Material.WHITE_CANDLE, 1));

		return shop;
	}

	// Figured out which item the Player clicked inside the Shop.
	public static void Click(Inventory shop, Player player, ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta != null) {
			// Get item's Display name and match it to the button.
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
				// If item's display name is null or doesn't match that above then Player is attempting to buy from the Shop.
				default -> BuyItem(shop, player, itemStack);
			}
		}
	}

	// Loads the Page before the current one the Player is looking at.
	private static void PreviousPage(Inventory shop, ItemMeta meta) {
		int page = Integer.parseInt(meta.getLore().get(0).split(" ")[1]) - 1;
		if (page >= 0)
			LoadPage(shop, page);
	}

	// Toggles the Paper Buttons between Previous/Next and First/Last.
	private static void SettingsPage(Inventory shop) {
		ItemStack itemStack = shop.getItem(49);
		int amount = itemStack.getAmount();
		if (amount == 1) {
			shop.setItem(slots - 9, UpdateDisplayName(shop.getItem(45), "First"));
			shop.setItem(slots - 1, UpdateDisplayName(shop.getItem(53), "Last"));
		}
		else {
			shop.setItem(slots - 9, UpdateDisplayName(shop.getItem(45), "Previous"));
			shop.setItem(slots - 1, UpdateDisplayName(shop.getItem(53), "Next"));
		}
		itemStack.setAmount(amount % 2 + 1);
		shop.setItem(slots - 5, itemStack);
	}

	// Loads the Page after the current one the Player is looking at.
	private static void NextPage(Inventory shop, ItemMeta meta) {
		int page = Integer.parseInt(meta.getLore().get(0).split(" ")[1]) + 1;
		int items = Main.getItemStacks().toArray().length;
		if ((slots - 9) * page < items)
			LoadPage(shop, page);
	}

	// Attempts to make Player buy an item from the Shop.
	private static void BuyItem(Inventory shop, Player player, ItemStack itemStack) {
		// Figure out if Player has an empty inventory slot.
		PlayerInventory inventory = player.getInventory();
		ItemStack[] slots = inventory.getContents();
		int slot = -1;
		for (int i = 0; i < 36; ++i) {
			if (slots[i] == null) {
				// If found, save slot position for later.
				slot = i;
				break;
			}
		}

		// If not then send inventory full message.
		if (slot == -1) {
			player.sendMessage("Your Inventory is Full!");
			return;
		}

		// Read price of item from itemStack provided.
		ItemMeta itemMeta = itemStack.getItemMeta();
		double price = Double.parseDouble(itemMeta.getLore().get(0).split("/")[0].split("\\$")[1]);

		// Get Player's bank balance.
		double balance = Main.econ.getBalance(player);

		// Calculate how much Player can afford to buy.
		int amount = itemStack.getAmount();
		if (amount * price > balance)
			amount = (int) Math.floor(balance / price);

		// If they can afford to buy at least 1, then...
		if (amount > 0) {
			// Remove the Lore from the item.
			itemMeta.setLore(null);
			itemStack.setItemMeta(itemMeta);
			itemStack.setAmount(amount);

			// Attempt to subtract amount from Shop, and save how much was actually subtracted.
			amount = Main.subtractItem(itemStack);
			final double cost = Math.round(amount * price * 100) / 100.0;

			// Charge Player for the amount of items that were removed from the Shop.
			Main.econ.withdrawPlayer(player, cost);

			// Give Player the amount of items that were removed from the Shop.
			itemStack.setAmount(amount);
			inventory.setItem(slot, itemStack);

			// Send Message Confirming Trade.
			player.sendMessage(Integer.toString(amount) + ' ' + itemStack.getType().name() + " bought for $" + cost);
			Bukkit.getLogger().info(player.getName() + " bought " + amount + ' ' + itemStack.getType().name() + " for $" + cost);

			// Refresh Page for Player.
			LoadPage(shop, Math.min(Integer.parseInt(shop.getItem(53).getItemMeta().getLore().get(0).split(" ")[1]), (int) Math.ceil((Main.getItemStacks().toArray().length - 1) / 45)));
		}
		// Else tell them they're poor.
		else
			player.sendMessage("You don't have enough Money to purchase that.");
	}

	// Load the Shop's items into the Inventory for the Player to observe.
	private static void LoadPage(Inventory shop, int page) {
		final int items = Main.getItemStacks().toArray().length;
		// Iterate over all the slots available for selling items.
		for (int i = 0; i < slots - 9; ++i) {
			// Calculate which position to pull out from itemStacks.
			int index = (slots - 9) * page + i;

			// If position is within the total items in the shop, then...
			if (index < items) {
				// Get itemStack from itemStacks.
				ItemStack itemStack = Main.getItemStacks().get(index).clone();

				// Update its Lore to include Price/Item and Stock available.
				ItemMeta itemMeta = itemStack.getItemMeta();
				List<String> subLines = new ArrayList<>();
				subLines.add("$" + Main.calcPrice(itemStack) + "/Item");
				subLines.add("Stock: " + itemStack.getAmount());
				itemMeta.setLore(subLines);
				itemStack.setItemMeta(itemMeta);

				// Set itemStack to it's max stackable size or stock available. Whichever is less.
				int maxAmount = itemStack.getMaxStackSize();
				if (maxAmount < itemStack.getAmount())
					itemStack.setAmount(maxAmount);

				// Put itemStack in Shop's Inventory.
				shop.setItem(i, itemStack);
			}
			// Else clear itemStack from Shop Inventory.
			else
				shop.setItem(i, null);
		}

		// Update Paper Buttons page number/ Add Paper Buttons if page is loading for the first time.
		ItemStack left = shop.getItem(slots - 9);
		shop.setItem(slots - 9, Utils.createItemStack(left == null ? "Previous" : left.getItemMeta().getDisplayName(), Material.PAPER, 1, "Page: " + page));
		ItemStack right = shop.getItem(slots - 1);
		shop.setItem(slots - 1, Utils.createItemStack(right == null ? "Next" : right.getItemMeta().getDisplayName(), Material.PAPER, 1, "Page: " + page));
	}

	// Update the Display Name of an Item.
	private static ItemStack UpdateDisplayName(ItemStack itemStack, String displayName) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	// Update the Inventory's stack sizes based off the provided changing amount.
	private static void UpdatePage(Inventory shop, int amount) {
		ItemStack[] itemStacks = shop.getContents();
		for (int i = 0; i < slots - 9; ++i) {
			ItemStack itemStack = itemStacks[i];
			if (itemStack != null) {
				itemStack.setAmount(Math.max(1, Math.min(amount + itemStack.getAmount(), Math.min(itemStack.getMaxStackSize(), Integer.parseInt(itemStack.getItemMeta().getLore().get(1).split(" ")[1])))));
				shop.setItem(i, itemStack);
			}
		}
	}
}
