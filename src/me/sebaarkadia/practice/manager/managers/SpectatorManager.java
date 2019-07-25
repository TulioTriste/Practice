package me.sebaarkadia.practice.manager.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.duel.Duel;
import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.util.UtilPlayer;

public class SpectatorManager extends Manager
{
    private Map<UUID, UUID> spectatingMatchUUID;
    
    public SpectatorManager(final ManagerHandler handler) {
        super(handler);
        this.spectatingMatchUUID = new HashMap<UUID, UUID>();
    }
    
    public void addSpectator(final Player player, final Player target) {
        final Duel duel = this.handler.getDuelManager().getDuelFromPlayer(target.getUniqueId());
        if (duel == null) {
            return;
        }
        for (final Player pls : Bukkit.getOnlinePlayers()) {
            pls.hidePlayer(player);
        }
        this.spectatingMatchUUID.put(player.getUniqueId(), duel.getUUID());
        duel.addSpectator(player.getUniqueId());
        if (duel.getFfaPlayers() != null) {
            for (final UUID uuid : duel.getFfaPlayersAlive()) {
                final Player player2 = this.handler.getPlugin().getServer().getPlayer(uuid);
                if (player2 == null) {
                    continue;
                }
                player.showPlayer(player2);
                player2.hidePlayer(player);
            }
        }
        else {
            for (final UUID uuid2 : duel.getFirstTeamAlive()) {
                final Player player3 = this.handler.getPlugin().getServer().getPlayer(uuid2);
                if (player3 == null) {
                    continue;
                }
                player.showPlayer(player3);
                player3.hidePlayer(player);
            }
            for (final UUID uuid2 : duel.getSecondTeamAlive()) {
                final Player player3 = this.handler.getPlugin().getServer().getPlayer(uuid2);
                if (player3 == null) {
                    continue;
                }
                player.showPlayer(player3);
                player3.hidePlayer(player);
            }
        }
        UtilPlayer.clear(player);
        final PracticePlayer practicePlayer = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        practicePlayer.setCurrentState(PlayerState.SPECTATING);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.teleport(target.getLocation().add(0.0, 3.5, 0.0));
        player.getInventory().setContents(this.handler.getItemManager().getSpecItems());
        player.updateInventory();
    }
    
    public void removeSpectator(final Player player, final boolean forced) {
        final Duel duel;
        if (this.spectatingMatchUUID.containsKey(player.getUniqueId()) && (duel = this.handler.getDuelManager().getDuelByUUID(this.spectatingMatchUUID.get(player.getUniqueId()))) != null && forced) {
            duel.getSpectators().remove(player.getUniqueId());
        }
        this.spectatingMatchUUID.remove(player.getUniqueId());
        this.handler.getPracticePlayerManager().sendToLobby(player);
    }
}
