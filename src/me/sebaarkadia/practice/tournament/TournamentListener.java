package me.sebaarkadia.practice.tournament;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;

import org.bukkit.event.player.PlayerQuitEvent;

import me.sebaarkadia.practice.Practice;

import org.bukkit.event.Listener;

public class TournamentListener implements Listener
{
    @SuppressWarnings("unlikely-arg-type")
	@EventHandler
    public void PlayerQuitEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (Tournament.getTournaments().size() == 0) {
            return;
        }
        for (final Tournament tournament : Tournament.getTournaments()) {
            if (tournament.isInTournament(player)) {
                if (tournament.getTournamentMatch(player) != null) {
                    player.setHealth(0.0);
                }
                final TournamentTeam tournamentTeam = tournament.getTournamentTeam(player);
                tournamentTeam.setPlayers(null);
                tournament.getTeams().remove(tournamentTeam);
                tournament.getCurrentQueue().remove(player);
                Practice.getInstance().getManagerHandler().getDuelManager().removePlayerFromDuel(player);
            }
        }
    }
    
    @EventHandler
    public void PlayerInteractEvent(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        for (final Tournament tournament : Tournament.getTournaments()) {
            if (tournament.isInTournament(p) && p.getLocation().getWorld() == Bukkit.getServer().getWorld("spawn")) {
                e.setCancelled(true);
            }
        }
    }
}
