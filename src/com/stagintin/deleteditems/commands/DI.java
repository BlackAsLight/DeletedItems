package com.stagintin.deleteditems.commands;

import com.stagintin.deleteditems.Main;
import com.stagintin.deleteditems.UI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DI implements CommandExecutor {
	public DI(Main plugin) {
		plugin.getCommand("di").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (!(commandSender instanceof Player)) {
			return true;
		}
		Player player = (Player) commandSender;
		player.openInventory(UI.GUI(player));
		return false;
	}
}
