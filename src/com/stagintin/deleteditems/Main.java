package com.stagintin.deleteditems;

import com.stagintin.deleteditems.commands.DI;
import com.stagintin.deleteditems.events.Damage;
import com.stagintin.deleteditems.events.Despawn;
import com.stagintin.deleteditems.events.InventoryClick;
import com.stagintin.deleteditems.events.InventoryDrag;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		// Add Config File.
		saveDefaultConfig();

		// Set up Items file.
		file = YamlConfiguration.loadConfiguration(new File(path + "items.yml"));
		try {
			itemStacks = (List<ItemStack>) file.getList("items");
		} catch (Exception error) {
			// Can't read file's content. Will be rewritten.
			getLogger().warning("Unable to read item.yml file. Will be written over!");
		}
		// If file is empty or had an error...
		if (itemStacks == null)
			itemStacks = new ArrayList<>();

		// Calculate the Total Items in the Shop and remove AIR from itemStacks if it exists in there somehow.
		int length = itemStacks.toArray().length;
		totalItems = 0;
		for (int i = 0; i < length; ++i) {
			ItemStack itemStack = itemStacks.get(i);
			if (itemStack.getType() == Material.AIR) {
				itemStacks.remove(itemStack);
				--i;
				--length;
			} else {
				totalItems += itemStack.getAmount();
			}
		}

		/* Dependencies
		-------------------------*/
		// Checks if Vault Plugin is available.
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().warning("Plugin Dependency: Vault is not detected!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// Checks if an Economy Plugin is available.
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

		/* Schedules
		-------------------------*/
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Main::reduceDurability, 20 * 60 * 30, 20 * 60 * 30); // Milliseconds * Seconds * Minutes

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

	// Calculate the Price of an Item for the Shop.
	public static double calcPrice(ItemStack itemStack) {
		// Calculate basePrice.
		double basePrice = ((double) (totalItems - itemStack.getAmount())) / itemStacks.toArray().length / itemStack.getType().getMaxStackSize();
		Damageable itemMeta = (Damageable) itemStack.getItemMeta();

		// Apply Enchantment Modifier to basePrice;
		basePrice *= calcEnchantModifier(itemMeta);

		// If itemStack has a durability that isn't zero, then...
		if (itemStack.getType().getMaxDurability() != 0)
			// Reduce price by a percentage of how much durability is left on itemStack.
			basePrice *= 1 - ((double) itemMeta.getDamage() / itemStack.getType().getMaxDurability());

		// Round to two decimal places and then return price.
		return Math.round(basePrice * 100) / 100.0;
	}

	// Calculate the Enchantment Modifier for the calcPrice function.
	public static double calcEnchantModifier(ItemMeta itemMeta) {
		double modifier = 0;
		// Iterate through enchantments on itemStack...
		for (Enchantment enchantment : itemMeta.getEnchants().keySet())
			// And calculate enchantment modifier.
			modifier += itemMeta.getEnchants().get(enchantment) * 5.0 / enchantment.getMaxLevel();

		// If itemStack had enchantments then return modifier, else return one to have no effect on the basePrice.
		return modifier == 0 ? 1 : modifier;
	}

	// Reduce all damaged items durability by one in the Shop and remove any that have no durability left.
	public static void reduceDurability() {
		int length = itemStacks.toArray().length;
		// Iterate through every item in itemStacks.
		for (int i = 0; i < length; ++i) {
			ItemStack itemStack = itemStacks.get(i);
			// If the itemStack is AIR, remove it from the Shop.
			if (itemStack.getType() == Material.AIR) {
				itemStacks.remove(itemStack);
				--i;
				--length;
				continue;
			}

			// If itemStack's max durability isn't zero then it is an item that can take damage.
			if (itemStack.getType().getMaxDurability() != 0) {
				Damageable itemMeta = (Damageable) itemStack.getItemMeta();
				// If itemStack has zero durability left then...
				if (itemMeta.getDamage() == itemStack.getType().getMaxDurability()) {
					// Remove itemStack from itemStacks.
					itemStacks.remove(itemStack);
					--i;
					--length;
				}
				// Else just reduce itemStack's durability by one.
				else {
					itemMeta.setDamage(itemMeta.getDamage() + 1);
					itemStacks.get(i).setItemMeta(itemMeta);
				}
			}
		}
	}

	// Add an Item to the Shop.
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

	// Get Shop's inventory for other places without giving access to edit the shop's inventory.
	public static List<ItemStack> getItemStacks() {
		return itemStacks;
	}

	// Remove an Item from the Shop.
	public static int subtractItem(ItemStack itemStack) {
		// Find provided itemStack in itemStacks.
		for (ItemStack stack : itemStacks)
			if (stack.getType() == itemStack.getType())
				if (stack.getItemMeta().hashCode() == itemStack.getItemMeta().hashCode()) {
					// Once found, calculate how much to remove from shop and remove it. Amount provided or if itemStacks has less, said less amount.
					int amount = Math.min(stack.getAmount(), itemStack.getAmount());
					stack.setAmount(stack.getAmount() - amount);

					// If amount left in itemStacks is zero...
					if (stack.getAmount() == 0)
						// Then remove the itemStack from itemStacks.
						itemStacks.remove(stack);

					// Remove the earlier calculated amount from totalItems.
					totalItems -= amount;

					itemUpdate();
					// Return the amount removed from itemStacks.
					return amount;
				}
		// Return zero as itemStack wasn't found in itemStacks.
		return 0;
	}

	// Sort, and Save the Shop's Inventory and recalculate the total amount of items in the shop encase error happened somewhere.
	private static void itemUpdate() {
		count %= 10;
		// On the tenth call of this function.
		if (count++ == 0) {
			// Sort itemStacks.
			itemStacks.sort((o1, o2) -> o2.getAmount() - o1.getAmount());

			// Save itemStacks to items.yml
			try {
				file.set("items", itemStacks);
				file.save(path + "items.yml");
			} catch (Exception e) {
				Bukkit.getLogger().warning("Failed to save items.yml.");
				Bukkit.getLogger().warning(e.getMessage());
			}

			// Recalculate totalItems in itemStacks.
			totalItems = 0;
			for (ItemStack stack : itemStacks)
				totalItems += stack.getAmount();
		}
	}
}
