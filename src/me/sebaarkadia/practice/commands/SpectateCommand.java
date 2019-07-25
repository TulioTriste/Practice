package me.sebaarkadia.practice.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.tournament.Tournament;

public class SpectateCommand implements CommandExecutor, TabCompleter
{
    private Practice plugin;
    
    public SpectateCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (settings.isMod()) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in Modmode" : "§cNo puedes ejecutar este comando en Modmode"));
            return true;
        }
        if (settings.isSeeAll()) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in Seeall Mode" : "§cNo puedes ejecutar este comando en SeeAll mode"));
            return true;
        }
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY && practicePlayer.getCurrentState() != PlayerState.SPECTATING) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar ese comando en este estado"));
            return true;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (!tournament.isInTournament(player)) {
                    continue;
                }
                return true;
            }
        }
        if (practicePlayer.getCurrentState() == PlayerState.SPECTATING) {
            this.plugin.getManagerHandler().getSpectatorManager().removeSpectator(player, true);
        }
        else if (args.length == 1) {
            final Player target = this.plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cPlayer not found." : "§cJugador no encontrado."));
                return true;
            }
            final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
            if (practiceTarget.getCurrentState() != PlayerState.FIGHTING) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe player is not in fight." : "§cEl jugador no esta en Duelo."));
                return true;
            }
            this.plugin.getManagerHandler().getSpectatorManager().addSpectator(player, target);
        }
        else {
            player.sendMessage(ChatColor.RED + "Usage: /spectate <player>");
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("spectate") && args.length == 1) {
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
