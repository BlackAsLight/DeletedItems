package com.stagintin.deleteditems;

import com.stagintin.deleteditems.commands.*;
import com.stagintin.deleteditems.events.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin {
	public final static String path = "plugins/DeletedItems/";
	public static Economy econ;
	private static YamlConfiguration file;
	private static List<ItemStack> itemStacks;
	private static int totalItems;
	private static int count = 0;

	@Override
	public void onEnable() {
		super.onEnable();

		/* Startup
		-------------------------*/
		// Add Config File
		saveDefaultConfig();

		// Set up Items file
		file = YamlConfiguration.loadConfiguration(new File(path + "items.yml"));
		try {
			itemStacks = (List<ItemStack>) file.getList("items");
		}
		catch (Exception error) {
			// Can't read file's content. Will be rewritten.
			getLogger().warning("Unable to read item.yml file. Will be written over!");
		}
		// If file is empty or had an error...
		if (itemStacks == null)
			itemStacks = new ArrayList<>();

		// Calculate the Total Weight
		totalItems = 0;
		for (ItemStack stack : itemStacks)
			totalItems += stack.getAmount();

		/* Dependencies
		-------------------------*/
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().warning("Plugin Dependency: Vault is not detected!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		else {
			RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp == null) {
				getLogger().warning("Plugin Dependency: No Economy Plugin detected!");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			econ = rsp.getProvider();
		}

		/* Commands
		-------------------------*/
		new DI(this);

		/* Events
		-------------------------*/
		new Damage(this);
		new Despawn(this);
		new InventoryClick(this);
		new InventoryDrag(this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Main::reduceDurability, 30 * 60 * 20, 30 * 60 * 20);

		getLogger().info("DeleteItems has been enabled! " + this.getConfig().getString("version"));
	}

	@Override
	public void onDisable() {
		super.onDisable();

		/* Shutdown
		-------------------------*/
		file.set("items", itemStacks);
		try {
			file.save(path + "items.yml");
		} catch (IOException e) {
			getLogger().warning("Failed to save items.yml");
			getLogger().warning(e.getMessage());
		}

		getLogger().info("DeleteItems has been disabled!");
	}

	public void logInfo(String message) {
		getLogger().info(message);
	}

	public static double calcPrice(ItemStack itemStack) {
		double basePrice = ((double)(totalItems - itemStack.getAmount())) / itemStacks.toArray().length / itemStack.getType().getMaxStackSize();
		Damageable itemMeta = (Damageable) itemStack.getItemMeta();
		basePrice *= calcEnchantModifier(itemMeta);
		if (itemStack.getType().getMaxDurability() != 0)
			basePrice *= 1 - ((double) itemMeta.getDamage() / itemStack.getType().getMaxDurability());
		return Math.round(basePrice * 100) / 100.0;
	}

	public static double calcEnchantModifier(ItemMeta itemMeta) {
		double modifier = 0;
		for (Enchantment enchantment : itemMeta.getEnchants().keySet())
			modifier += itemMeta.getEnchants().get(enchantment) * 5.0 / enchantment.getMaxLevel();
		return modifier == 0 ? 1 : modifier;
	}

	public static void reduceDurability() {
		int length = itemStacks.toArray().length;
		for (int i = 0; i < length; ++i) {
			ItemStack itemStack = itemStacks.get(i);
			if (itemStack.getType().getMaxDurability() != 0) {
				Damageable itemMeta = (Damageable) itemStack.getItemMeta();
				if (itemMeta.getDamage() != 0) {
					if (itemMeta.getDamage() == itemStack.getType().getMaxDurability()) {
						itemStacks.remove(itemStack);
						--i;
						--length;
					}
					else {
						itemMeta.setDamage(itemMeta.getDamage() + 1);
						itemStacks.get(i).setItemMeta(itemMeta);
					}
				}
			}
		}
	}

	public static void addItem(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(null);
		itemStack.setItemMeta(itemMeta);

		// Look if ItemStack already exists.
		boolean found = false;
		for (ItemStack stack : itemStacks)
			if (stack.getType() == itemStack.getType())
				if (stack.getItemMeta().hashCode() == itemMeta.hashCode()) {
					// If so increase amount in list.
					stack.setAmount(stack.getAmount() + itemStack.getAmount());
					found = true;
					break;
				}

		// If not, add itemStack to end of list.
		if (!found)
			itemStacks.add(itemStack);

		totalItems += itemStack.getAmount();
		itemUpdate();
	}

	public static List<ItemStack> getItemStacks() {
		return itemStacks;
	}

	public static int subtractItem(ItemStack itemStack) {
		for (ItemStack stack : itemStacks)
			if (stack.getType() == itemStack.getType())
				if (stack.getItemMeta().hashCode() == itemStack.getItemMeta().hashCode()) {
					int amount = Math.min(stack.getAmount(), itemStack.getAmount());
					stack.setAmount(stack.getAmount() - amount);
					if (stack.getAmount() == 0)
						itemStacks.remove(stack);
					totalItems -= amount;
					itemUpdate();
					return amount;
				}
		return 0;
	}

	private static void itemUpdate() {
		count %= 10;
		if (count++ == 0) {
			// Save Content to File
			itemStacks.sort((o1, o2) -> o2.getAmount() - o1.getAmount());
			try {
				file.set("items", itemStacks);
				file.save(path + "items.yml");
			}
			catch (Exception e) {
				Bukkit.getLogger().warning("Failed to save items.yml.");
				Bukkit.getLogger().warning(e.getMessage());
			}
			totalItems = 0;
			for (ItemStack stack : itemStacks)
				totalItems += stack.getAmount();
		}
	}
}
