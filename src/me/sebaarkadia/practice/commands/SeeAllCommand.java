package me.sebaarkadia.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;

public class SeeAllCommand implements CommandExecutor
{
    private Practice plugin;
    
    public SeeAllCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (!player.hasPermission("practice.commands.seeall")) {
            player.sendMessage("No Permission!");
            return true;
        }
        if (!settings.isMod()) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou must be in modmode" : "§cDebes estar en modmode"));
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar este comando en tu estado"));
            return true;
        }
        if (!settings.isSeeAll()) {
            settings.setSeeAll(!settings.isSeeAll());
            for (final Player pls : Bukkit.getOnlinePlayers()) {
                player.showPlayer(pls);
            }
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§aSeeAll is enabled" : "§aSeeAll esta activado"));
            return true;
        }
        settings.setSeeAll(!settings.isSeeAll());
        for (final Player pls : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(pls);
        }
        player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cSeeAll is disabled" : "§cSeeAll esta desactivado"));
        return true;
    }
}
