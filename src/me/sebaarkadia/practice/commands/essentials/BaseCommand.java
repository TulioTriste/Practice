package me.sebaarkadia.practice.commands.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.sebaarkadia.practice.Practice;

public abstract class BaseCommand {

	public Practice instance;
	public boolean forPlayerUseOnly;

	public String command;
	public String permission;

	public BaseCommand(Practice plugin) {
		this.instance = plugin;

		this.command = "";
		this.permission = "";

		this.forPlayerUseOnly = false;

	}
	public abstract void execute(CommandSender sender, String arg, String[] args);
	
	public Practice getInstance() {
		return this.instance;
	}
}
