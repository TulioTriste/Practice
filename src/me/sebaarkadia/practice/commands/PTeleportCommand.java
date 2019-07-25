package me.sebaarkadia.practice.commands;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.Bukkit;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class PTeleportCommand implements CommandExecutor, TabCompleter
{
    private Practice plugin;
    
    public PTeleportCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        final Player player = (Player)sender;
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (!player.hasPermission("practice.commands.pteleport")) {
            player.sendMessage("No Permission!");
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar ese comando en tu estado"));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /ptp <player>");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Usage: /ptp <player>");
            return true;
        }
        else {
            Player toCheck = Bukkit.getPlayer(StringUtils.join((Object[])args));
            if (toCheck == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + StringUtils.join((Object[])args) + "' found online.");
                return true;
            }
            final Player target;
            toCheck = (target = Bukkit.getPlayer(StringUtils.join((Object[])args)));
            if (player.getName() != target.getName()) {
                player.teleport((Entity)target);
                player.sendMessage(String.valueOf(String.valueOf(settings.isPublicChat() ? "§aTeleported to " : "§aTeletransportado ha ")) + target.getName());
                for (final Player pls : Bukkit.getOnlinePlayers()) {
                    if (pls.isOp()) {
                        pls.getPlayer().sendMessage("§7§o[" + player.getName() + ": P Teleported " + player.getName() + " to " + target.getName() + "]");
                    }
                }
            }
            return true;
        }
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("ptp") && args.length == 1) {
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
