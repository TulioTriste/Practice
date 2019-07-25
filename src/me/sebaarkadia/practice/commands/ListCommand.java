package me.sebaarkadia.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("list")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "There are currently " + ChatColor.YELLOW.toString() + ChatColor.BOLD + Bukkit.getOnlinePlayers().size() + " out of " + Bukkit.getMaxPlayers() + ChatColor.GOLD.toString() + ChatColor.BOLD + " connected to the server.");
		}
		return true;
	}

}
