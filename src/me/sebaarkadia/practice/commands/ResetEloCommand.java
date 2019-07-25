package me.sebaarkadia.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;

public class ResetEloCommand implements CommandExecutor
{
    private Practice plugin;
    
    public ResetEloCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage("§cSorry! You don't");
            return true;
        }
        if (practicePlayer.getCredits() <= 0) {
            player.sendMessage(ChatColor.RED + "You don't have enough credits.");
            return true;
        }
        practicePlayer.setCredits(practicePlayer.getCredits() - 1);
        for (final Kit kit : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
            practicePlayer.addElo(kit.getName(), 1000);
        }
        player.sendMessage(ChatColor.GREEN + "Your ELO has been reset.");
        return true;
    }
}
