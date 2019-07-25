package me.sebaarkadia.practice.commands.essentials;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.commands.essentials.chat.BroadcastCommand;
import me.sebaarkadia.practice.util.Color;
import me.sebaarkadia.practice.util.Handler;

public class CommandHandler extends Handler implements CommandExecutor {
	
	private List<BaseCommand> commands;

	public CommandHandler(Practice plugin) {
		super(plugin);

		this.commands = new ArrayList<BaseCommand>();

		this.commands.add(new BroadcastCommand(plugin));

		for (BaseCommand baseCommand : this.commands) {
			getInstance().getCommand(baseCommand.command).setExecutor(this);
		}
	}

	public void disable() {
		this.commands.clear();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		for (BaseCommand baseCommand : this.commands) {
			if (cmd.getName().equalsIgnoreCase(baseCommand.command)) {
				if (((sender instanceof ConsoleCommandSender)) && (baseCommand.forPlayerUseOnly)) {
					sender.sendMessage(Color.translate("&cThis command is not execute in console!"));
					return true;
				}
				if ((!sender.hasPermission(baseCommand.permission)) && (!baseCommand.permission.equals(""))) {
					sender.sendMessage(Color.translate("&cYou do not have permissions!"));
					return true;
				}
				baseCommand.execute(sender, label, args);
				return true;
			}
		}
		return true;
	}
}
