package me.sebaarkadia.practice.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.util.UtilPlayer;

public class PingCommand implements CommandExecutor, TabCompleter
{
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        Player toCheck;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /ping <player>");
                return true;
            }
            toCheck = (Player)sender;
        }
        else {
            toCheck = Bukkit.getPlayer(StringUtils.join((Object[])args));
        }
        if (toCheck == null) {
            sender.sendMessage(ChatColor.RED + "No player named '" + StringUtils.join((Object[])args) + "' found online.");
            return true;
        }
        if (UtilPlayer.getPing(toCheck) == 0) {
            sender.sendMessage("§6§l" + toCheck.getName() + (toCheck.getName().endsWith("s") ? "'" : "'s") + " §ecurrent ping is §6§l200ms§e.");
            if (sender instanceof Player && !toCheck.getName().equals(sender.getName())) {
                final Player senderPlayer = (Player)sender;
                sender.sendMessage("§ePing difference: §6§l" + (Math.max(UtilPlayer.getPing(senderPlayer), 200) - Math.min(UtilPlayer.getPing(senderPlayer), 200)) + "ms§e.");
            }
        }
        else {
            sender.sendMessage("§6§l" + toCheck.getName() + (toCheck.getName().endsWith("s") ? "'" : "'s") + " §ecurrent ping is §6§l" + UtilPlayer.getPing(toCheck) + "ms§e.");
            if (sender instanceof Player && !toCheck.getName().equals(sender.getName())) {
                final Player senderPlayer = (Player)sender;
                sender.sendMessage("§ePing difference: §6§l" + (Math.max(UtilPlayer.getPing(senderPlayer), UtilPlayer.getPing(toCheck)) - Math.min(UtilPlayer.getPing(senderPlayer), UtilPlayer.getPing(toCheck))) + "ms§e.");
            }
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("ping") && args.length == 1) {
            final ArrayList<String> playerName = new ArrayList<String>();
            if (!args[0].equals("")) {
                for (final Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        playerName.add(pl.getName());
                    }
                }
            }
            else {
                for (final Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        playerName.add(pl.getName());
                    }
                }
            }
            Collections.sort(playerName);
            return playerName;
        }
        return null;
    }
}
