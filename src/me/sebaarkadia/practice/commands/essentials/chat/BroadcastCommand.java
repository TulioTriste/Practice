package me.sebaarkadia.practice.commands.essentials.chat;

import org.bukkit.command.CommandSender;
import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.commands.essentials.BaseCommand;
import me.sebaarkadia.practice.util.Color;

public class BroadcastCommand extends BaseCommand {

	public BroadcastCommand(Practice plugin) {
		super(plugin);
		
		this.command = "broadcast";
		this.permission = "practice.command.broadcast";
	}

	@Override
	public void execute(CommandSender sender, String arg, final String[] args) {		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /broadcast <message>"));
		} else {
			
			StringBuilder message = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				message.append(args[i]).append(" ");
			}
			Color.sendMessage(Color.translate(Practice.getInstance().getConfig().getString("BROADCAST") + message.toString().replaceAll("&", "§")));
		}
		return;
	}
}
