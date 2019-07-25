package me.sebaarkadia.practice.commands;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.util.Color;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class BuilderCommand implements CommandExecutor
{
    private Practice plugin;
    
    public BuilderCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (!player.hasPermission("practice.commands.builder")) {
            player.sendMessage("No Permission!");
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() == PlayerState.BUILDER) {
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            player.sendMessage(String.valueOf(Color.translate(settings.isPublicChat() ? "&cBuild mod is disable" : "&cModo Builder desactivado")));
            return true;
        }
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(String.valueOf((settings.isPublicChat() ? "&cCannot execute this command in your current state" : "&cNo puedes ejecutar ese comando en este estado")));
            return true;
        }
        practicePlayer.setCurrentState(PlayerState.BUILDER);
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(String.valueOf(Color.translate(settings.isPublicChat() ? "&aBuild mod is enable" : "&aModo Builder activado")));
        return true;
    }
}
