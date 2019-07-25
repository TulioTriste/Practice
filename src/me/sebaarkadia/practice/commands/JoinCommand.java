package me.sebaarkadia.practice.commands;

import java.util.Collections;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.runnables.UpdateInventoryTask;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.tournament.Tournament;
import me.sebaarkadia.practice.tournament.TournamentTeam;
import me.sebaarkadia.practice.util.UtilPlayer;
import net.md_5.bungee.api.ChatColor;

public class JoinCommand implements CommandExecutor
{
    private Practice plugin;
    
    public JoinCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY && practicePlayer.getCurrentState() != PlayerState.SPECTATING) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar ese comando en tu estado"));
            return true;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (tournament.isInTournament(player)) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are already in a tournament." : "Ya estas en el tournament."));
                    return true;
                }
            }
        }
        if (args.length == 0) {
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "There are no tournaments available." : "No hay tournaments no esta disponible."));
                return true;
            }
            player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getJoinTournamentInventory());
        }
        else if (args.length == 1) {
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "There are no tournaments available." : "No hay tournaments no esta disponible."));
                return true;
            }
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (tournament.isInTournament(player)) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are currently in another tournament." : "Ya estas anotado en el tournament."));
                    return true;
                }
            }
            final int id = Integer.parseInt(args[0]);
            final Tournament tournament2 = Tournament.getTournaments().get(id - 1);
            if (tournament2 == null) {
                player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "That tournament id doesn't exist." : "Esta id tournament no existe"));
                return true;
            }
            if (tournament2.isStarted()) {
                player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "The tournament already started." : "El tournament ya ha iniciado."));
                return true;
            }
            if (tournament2.getTotalPlayersInTournament() == tournament2.getPlayersLimit()) {
                player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "The tournament is already full." : "El tournament ya se ha llenado."));
                return true;
            }
            if (tournament2.isInTournament(player)) {
                player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are already in the tournament." : "Ya estas en el tournament."));
                return true;
            }
            if (tournament2.getMaximumPerTeam() == 1) {
                final TournamentTeam tournamentTeam = new TournamentTeam();
                tournamentTeam.setPlayers(Collections.singletonList(player.getUniqueId()));
                tournament2.getTeams().add(tournamentTeam);
                UtilPlayer.clear2(player);
                player.getInventory().setContents(Practice.getInstance().getManagerHandler().getItemManager().getTourItems());
                tournament2.sendMessage(ChatColor.GREEN + player.getName() + " has joined the tournament. (" + tournament2.getTotalPlayersInTournament() + "/" + tournament2.getPlayersLimit() + ")");
            }
            else if (tournament2.getMaximumPerTeam() >= 2) {
                final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                if (party == null) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You must be in a party to join this tournament." : "Debes estar en una party para unirte.."));
                    return true;
                }
                if (party.getLeader() != player.getUniqueId()) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Only the leader can join the tournament." : "Solamente el jefe puede unirse."));
                    return true;
                }
                if (party.getSize() != tournament2.getMaximumPerTeam()) {
                    if (settings.isPublicChat()) {
                        player.sendMessage(ChatColor.RED + "The party must have only " + tournament2.getMaximumPerTeam() + " players.");
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "La party debe tener " + tournament2.getMaximumPerTeam() + " jugadores.");
                    }
                    return true;
                }
                final TournamentTeam tournamentTeam2 = new TournamentTeam();
                tournamentTeam2.setPlayers(party.getAllMembersOnline());
                tournament2.getTeams().add(tournamentTeam2);
                tournament2.sendMessage(ChatColor.YELLOW + player.getName() + "'s Party has joined the tournament. (" + tournament2.getTotalPlayersInTournament() + "/" + tournament2.getPlayersLimit() + ")");
                this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Usage: /join");
        }
        return true;
    }
}
