package me.sebaarkadia.practice.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.runnables.UpdateInventoryTask;
import me.sebaarkadia.practice.tournament.Tournament;
import me.sebaarkadia.practice.tournament.TournamentTeam;

public class LeaveCommand implements CommandExecutor
{
    private Practice plugin;
    
    public LeaveCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY && practicePlayer.getCurrentState() != PlayerState.SPECTATING) {
            player.sendMessage("§cCannot execute this command in your current state.");
            return true;
        }
        if (args.length == 0) {
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage("§cThere are no tournaments available.");
                return true;
            }
            Tournament tournament = Tournament.getTournaments().get(0);
            if (tournament.getTotalPlayersInTournament() == tournament.getPlayersLimit()) {
                player.sendMessage("§cThe tournament is full.");
                return true;
            }
            final List<Tournament> check = new ArrayList<Tournament>();
            for (final Tournament tournaments1 : Tournament.getTournaments()) {
                check.add(tournaments1);
            }
            for (final Tournament tournaments2 : Tournament.getTournaments()) {
                if (!tournaments2.isInTournament(player)) {
                    if (check.size() == 0) {
                        player.sendMessage("§cYou don't.");
                        return true;
                    }
                    check.remove(tournaments2);
                }
                else {
                    tournament = tournaments2;
                }
            }
            TournamentTeam tournamentTeam = tournament.getTournamentTeam(player);
            tournamentTeam = tournament.getTournamentTeam(player);
            if (tournamentTeam == null) {
                player.sendMessage("§cYou don't.");
                return true;
            }
            if (tournament.getTournamentMatch(player) != null) {
                player.sendMessage(ChatColor.RED + "You can't leave during a match.");
                return true;
            }
            final String reason = (tournamentTeam.getPlayers().size() > 1) ? "Someone in your party left the tournament" : "You left the tournament";
            tournamentTeam.sendMessage(ChatColor.RED + "You have been removed from the tournament.");
            tournamentTeam.sendMessage(ChatColor.RED + "Reason: " + ChatColor.GRAY + reason);
            tournament.getCurrentQueue().remove(tournamentTeam);
            tournament.getTeams().remove(tournamentTeam);
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
        }
        return true;
    }
}
