package com.stagintin.deleteditems.commands;

import com.stagintin.deleteditems.Main;
import com.stagintin.deleteditems.UI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DI implements CommandExecutor {
	private Main plugin;
	public DI(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("di").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		plugin.getLogger().info("DI Executed!");
		if (!(commandSender instanceof Player)) {
			return true;
		}
		Player p = (Player) commandSender;
		p.openInventory(UI.GUI(p));
		return false;
	}
}
