package me.sebaarkadia.practice.commands;

import java.util.Map;
import org.bukkit.plugin.Plugin;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PracticePlayer;

import org.bukkit.ChatColor;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class CreditsCommand implements CommandExecutor
{
    private Practice plugin;
    
    public CreditsCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission("command.credits.admin")) {
            sender.sendMessage("§cNo Permission!");
            return true;
        }
        if (args.length == 0) {
            final Player player = (Player)sender;
            player.sendMessage("§cIncorrect usage.");
            return true;
        }
        if (args.length == 2) {
            @SuppressWarnings("unused")
			String name = "";
            final Player player2 = Bukkit.getPlayer(args[0]);
            UUID uuid;
            if (player2 != null) {
                uuid = player2.getUniqueId();
                name = player2.getName();
            }
            else {
                try {
                    final Map.Entry<UUID, String> recipient = PracticePlayer.getExternalPlayerInformation(args[0]);
                    uuid = recipient.getKey();
                    name = recipient.getValue();
                }
                catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
            }
            final int amount = Integer.parseInt(args[1]);
            final PracticePlayer practicePlayer = PracticePlayer.getByUuid(uuid);
            if (practicePlayer == null) {
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    practicePlayer.setCredits(practicePlayer.getCredits() + amount);
                    practicePlayer.save();
                }
            });
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                Bukkit.getPlayer(uuid).sendMessage("§3You receive §9" + String.valueOf(amount) + "§3 credits");
            }
            sender.sendMessage("§3You gived §9" + String.valueOf(amount) + "§3 credits to §9" + player2.getName());
        }
        else {
            sender.sendMessage("§cIncorrect usage.");
        }
        return true;
    }
}
