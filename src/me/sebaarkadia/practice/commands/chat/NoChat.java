package me.sebaarkadia.practice.commands.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.util.Color;

import org.bukkit.command.CommandExecutor;

public class NoChat implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&cYou must be player to execute this command."));
            return true;
        }
        final Player player = (Player)sender;
        if (!player.isOp()) {
        	sender.sendMessage(Color.translate("&cThis chat is currently muted"));
            return true;
        }
        sender.sendMessage(Color.translate("&cThis chat is currently muted"));
        return true;
    }
}