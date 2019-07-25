package me.sebaarkadia.practice.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.util.Color;
import me.sebaarkadia.practice.util.cooldown.Cooldowns;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class NewVideoCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&cYou must be player to execute this command."));
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.command.media")) {
            player.sendMessage(Color.translate("&cYou do not have permissions!"));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <videoLink>"));
        }
        else {
            if (Cooldowns.isOnCooldown("newvideo_delay", player)) {
                player.sendMessage(Color.translate("&cPlease, wait &l" + Cooldowns.getCooldownInt("newvideo_delay", player) + " &cto use again."));
                return true;
            }
            Bukkit.broadcastMessage(Color.translate("&7&m-------------------------------------"));
            Bukkit.broadcastMessage(Color.translate("&6" + player.getName() + " &euploaded a new video!"));
            Bukkit.broadcastMessage(Color.translate("&6Video Link: &7" + StringUtils.join((Object[])args, ' ', 0, args.length).replace("https://www.youtube.com/watch?v=", "youtu.be/")).replace("https://", ""));
            Bukkit.broadcastMessage(Color.translate("&7&m-------------------------------------"));
            Cooldowns.addCooldown("newvideo_delay", player, 60);
        }
        return true;
    }
}
