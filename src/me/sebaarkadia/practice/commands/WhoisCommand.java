package me.sebaarkadia.practice.commands;

import org.bukkit.entity.Player;

import me.sebaarkadia.practice.util.Color;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class WhoisCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String Label, final String[] arguments) {
        if (sender.hasPermission("practice.command.whois")) {
            if (arguments.length == 0) {
                sender.sendMessage(Color.translate("&cUsage: /" + Label + " <playerName>"));
            }
            else if (arguments.length == 1) {
                final Player target = Bukkit.getPlayer(arguments[0]);
                if (target != null) {
                    sender.sendMessage(Color.translate("&7&m---------------------------"));
                    sender.sendMessage(Color.translate("&a[" + target.getDisplayName() + "&a]"));
                    sender.sendMessage(Color.translate("&eHunger: &b" + target.getFoodLevel() + "/" + 20 + " (" + target.getSaturation() + " saturation)"));
                    sender.sendMessage(Color.translate("&eOperator: &b" + target.isOp()));
                    sender.sendMessage(Color.translate("&eGamemode: &b" + target.getGameMode().name().replace("_", " ")));
                    sender.sendMessage(Color.translate("&eIPv4 Adress: &b" + (sender.hasPermission("practice.whois.viewip") ? target.getAddress().getHostString() : new StringBuilder().append("1.1.1.1").toString())));
                    sender.sendMessage(Color.translate("&7&m---------------------------"));
                }
                else {
                    sender.sendMessage(Color.translate("&cPlayer '&7" + arguments[0] + "&c' not found."));
                }
            }
        }
        else {
            sender.sendMessage(Color.translate("&cYou do not have permissions!"));
        }
        return true;
    }
}
