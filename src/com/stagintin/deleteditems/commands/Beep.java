package com.stagintin.deleteditems.commands;

import com.stagintin.deleteditems.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Beep implements CommandExecutor {
	private Main plugin;
	public Beep(Main plugin){
		this.plugin = plugin;
		plugin.getCommand("beep").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		commandSender.sendMessage("boop!");
		return true;
	}
}
