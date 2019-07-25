package me.sebaarkadia.practice.commands;

import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.scoreboard.PlayerBoard;
import me.sebaarkadia.practice.scoreboard.sidebar.DuelScoreboardProvider;
import me.sebaarkadia.practice.scoreboard.sidebar.LobbyScoreboardProvider;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class ScoreboardCommand implements CommandExecutor
{
    private Practice plugin;
    
    public ScoreboardCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        final boolean toggle = practicePlayer.isScoreboard();
        final PlayerBoard playerBoard = this.plugin.getManagerHandler().getScoreboardHandler().getPlayerBoard(player.getUniqueId());
        if (toggle) {
            if (playerBoard != null) {
                playerBoard.setSidebarVisible(false);
            }
            practicePlayer.setScoreboard(false);
        }
        else {
            if (playerBoard != null) {
                playerBoard.setSidebarVisible(true);
            }
            practicePlayer.setScoreboard(true);
        }
        if (practicePlayer.getCurrentState() == PlayerState.LOBBY || practicePlayer.getCurrentState() == PlayerState.QUEUE) {
            if (playerBoard != null) {
                playerBoard.setDefaultSidebar(new LobbyScoreboardProvider(this.plugin), 2L);
            }
        }
        else if ((practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) && playerBoard != null) {
            playerBoard.setDefaultSidebar(new DuelScoreboardProvider(this.plugin), 2L);
        }
        return true;
    }
}
