package com.stagintin.deleteditems;

import com.stagintin.deleteditems.commands.*;
import com.stagintin.deleteditems.events.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin {
	public final static String path = "plugins/DeletedItems/";
	private static Economy econ;
	private static YamlConfiguration file;
	public static List<ItemStack> itemStacks;
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
		new Beep(this);
		new DI(this);

		/* Events
		-------------------------*/
		new Damage(this);
		new Despawn(this);
		new InventoryClick(this);
		new InventoryDrag(this);

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

	public static void addItem(ItemStack itemStack) throws IOException {
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

		count %= 10;
		if (count++ == 0) {
			// Save Content to File
			itemStacks.sort((o1, o2) -> o2.getAmount() - o1.getAmount());
			file.set("items", itemStacks);
			file.save(path + "items.yml");
		}
	}
}
