package me.sebaarkadia.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.util.Color;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class GamemodeCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&cYou must be player to execute this command."));
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.command.gamemode")) {
            player.sendMessage(Color.translate("&cYou do not have permissions!"));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <mode> <playerName>"));
        }
        else if (args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0")) {
            if (args.length != 2) {
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    player.sendMessage(Color.translate("&cYou already in Survival mode."));
                }
                else {
                    player.setGameMode(GameMode.SURVIVAL);
                    Command.broadcastCommandMessage(player, Color.translate("&eSet own gamemode to &bSurvival&e."));
                }
            }
            else {
                final Player target = Bukkit.getPlayer(args[1]);
                if (!GamemodeCommand.isOnline(player, target)) {
                    GamemodeCommand.PLAYER_NOT_FOUND(player, args[1]);
                    return true;
                }
                if (target.getGameMode() == GameMode.SURVIVAL) {
                    player.sendMessage(Color.translate("&c" + target.getName() + " is already in Survival mode."));
                }
                else {
                    target.setGameMode(GameMode.SURVIVAL);
                    Command.broadcastCommandMessage(player, Color.translate("&eSet gamemode of &b" + target.getName() + " &eto &bSurvival&e."));
                }
            }
        }
        else if (args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1")) {
            if (args.length != 2) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.sendMessage(Color.translate("&cYou already in Creative mode."));
                }
                else {
                    player.setGameMode(GameMode.CREATIVE);
                    Command.broadcastCommandMessage(player, Color.translate("&eSet own gamemode to &bCreative&e."));
                }
            }
            else {
                final Player target = Bukkit.getPlayer(args[1]);
                if (!GamemodeCommand.isOnline(player, target)) {
                    GamemodeCommand.PLAYER_NOT_FOUND(player, args[1]);
                    return true;
                }
                if (target.getGameMode() == GameMode.CREATIVE) {
                    player.sendMessage(Color.translate("&c" + target.getName() + " is already in Creative mode."));
                }
                else {
                    target.setGameMode(GameMode.CREATIVE);
                    Command.broadcastCommandMessage(player, Color.translate("&eSet gamemode of &b" + target.getName() + " &eto &bCreative&e."));
                }
            }
        }
        else if (args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2")) {
            if (args.length != 2) {
                if (player.getGameMode() == GameMode.ADVENTURE) {
                    player.sendMessage(Color.translate("&cYou already in Adventure mode."));
                }
                else {
                    player.setGameMode(GameMode.ADVENTURE);
                    Command.broadcastCommandMessage(player, Color.translate("&eSet own gamemode to &bAdventure&e."));
                }
            }
            else {
                final Player target = Bukkit.getPlayer(args[1]);
                if (!GamemodeCommand.isOnline(player, target)) {
                    GamemodeCommand.PLAYER_NOT_FOUND(player, args[1]);
                    return true;
                }
                if (target.getGameMode() == GameMode.ADVENTURE) {
                    player.sendMessage(Color.translate("&c" + target.getName() + " is already in Adventure mode."));
                }
                else {
                    target.setGameMode(GameMode.ADVENTURE);
                    Command.broadcastCommandMessage(player, Color.translate("&eSet gamemode of &b" + target.getName() + " &eto &bAdventure&e."));
                }
            }
        }
        else {
            player.sendMessage(Color.translate("&cGamemode sub-command '" + args[0] + "' not found."));
        }
        return true;
    }
    
    public static boolean isOnline(final CommandSender sender, final Player player) {
        return player != null && (!(sender instanceof Player) || ((Player)sender).canSee(player));
    }
    
    public static void PLAYER_NOT_FOUND(final CommandSender sender, final String player) {
        sender.sendMessage(Color.translate("&cPlayer '&7" + player + "&c' is currently offline."));
    }
}
