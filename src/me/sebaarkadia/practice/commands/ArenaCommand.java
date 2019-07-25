package me.sebaarkadia.practice.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.arena.Arena;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class ArenaCommand implements CommandExecutor
{
    private Practice plugin;
    
    public ArenaCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] commandArgs) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (commandArgs.length == 0) {
            player.sendMessage("§7§m--------------------------------------------");
            player.sendMessage("    §6§lArena §7- §eCommands");
            player.sendMessage("");
            player.sendMessage(" §7- §e/arena create §7{arenaname}");
            player.sendMessage(" §7- §e/arena remove§7{arenaname}");
            player.sendMessage(" §7- §e/arena set1location §7{arenaname}");
            player.sendMessage(" §7- §e/arena set2location §7{arenaname}");
            player.sendMessage(" §7- §e/arena enable §7{arenaname}");
            player.sendMessage(" §7- §e/arena disable §7{arenaname}");
            player.sendMessage("§7§m--------------------------------------------");
            return true;
        }
        if (!player.hasPermission("practice.commands.arena")) {
            player.sendMessage("§cNo Permission!");
            return true;
        }
        if (commandArgs[0].equalsIgnoreCase("create")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena create <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            if (this.plugin.getManagerHandler().getArenaManager().getArena(arenaName) != null) {
                player.sendMessage(ChatColor.RED + "This arena already exists!");
                return true;
            }
            this.plugin.getManagerHandler().getArenaManager().createArena(arenaName);
            player.sendMessage(ChatColor.YELLOW + "Successfully created the arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + "!");
            Practice.getInstance().saveConfig();
        }
        else if (commandArgs[0].equalsIgnoreCase("set1location")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena set1location <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setFirstTeamLocation(player.getLocation());
            player.sendMessage(ChatColor.YELLOW + "Successfully set the first team's location in arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + "!");
            Practice.getInstance().saveConfig();
        }
        else if (commandArgs[0].equalsIgnoreCase("set2location")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena set2location <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setSecondTeamLocation(player.getLocation());
            player.sendMessage(ChatColor.YELLOW + "Successfully set the second team's location in arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + "!");
            Practice.getInstance().saveConfig();
        }
        else if (commandArgs[0].equalsIgnoreCase("enable") || commandArgs[1].equalsIgnoreCase("disable")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena enable <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setEnabled(!arena.isEnabled());
            player.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + " is now " + ChatColor.GREEN + (arena.isEnabled() ? "enabled" : "disabled") + ChatColor.YELLOW + "!");
            Practice.getInstance().saveConfig();
        }
        else if (commandArgs[0].equalsIgnoreCase("remove")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena remove <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            if (this.plugin.getManagerHandler().getArenaManager().getArena(arenaName) == null) {
                player.sendMessage(ChatColor.RED + "This arena does not exist!");
                return true;
            }
            this.plugin.getManagerHandler().getArenaManager().destroyArena(arenaName);
            player.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + " has been removed!");
            Practice.getInstance().saveConfig();
        }
        else {
            player.sendMessage("§cIncorrect usage.");
        }
        return true;
    }
}
