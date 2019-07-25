package me.sebaarkadia.practice.manager.managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.UUID;
import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.listeners.PlayerListener;
import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.runnables.LoadPlayerTask;
import me.sebaarkadia.practice.runnables.SavePlayerConfig;
import me.sebaarkadia.practice.scoreboard.PlayerBoard;
import me.sebaarkadia.practice.scoreboard.sidebar.LobbyScoreboardProvider;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.util.PlayerUtility;
import me.sebaarkadia.practice.util.UtilItem;
import me.sebaarkadia.practice.util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PracticePlayerManager extends Manager
{
    public PracticePlayerManager(final ManagerHandler handler) {
        super(handler);
    }
    
    public void disable() {
        for (final Player player : PlayerUtility.getOnlinePlayers()) {
            new SavePlayerConfig(player.getUniqueId(), this.handler.getPlugin()).run();
        }
    }
    
    private void loadPlayerData(final PracticePlayer player) {
        player.setCurrentState(PlayerState.LOADING);
        Bukkit.getScheduler().runTaskAsynchronously(this.handler.getPlugin(), new LoadPlayerTask(this.handler.getPlugin(), player));
    }
    
    public void createPracticePlayer(final Player player) {
        final PracticePlayer practicePlayer = new PracticePlayer(player.getUniqueId(), true);
        this.sendToLobby(player);
        this.loadPlayerData(practicePlayer);
    }
    
    public PracticePlayer getPracticePlayer(final Player player) {
        return PracticePlayer.getByUuid(player.getUniqueId());
    }
    
    public PracticePlayer getPracticePlayer(final UUID uuid) {
        return PracticePlayer.getByUuid(uuid);
    }
    
    public void removePracticePlayer(final Player player) {
        this.handler.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.handler.getPlugin(), new SavePlayerConfig(player.getUniqueId(), this.handler.getPlugin()));
    }
    
    public void sendToLobbyQueueing(final Player player) {
        Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        PlayerListener.getLastPearl().remove(player.getUniqueId());
        UtilPlayer.clear2(player);
        final PracticePlayer practicePlayer = PracticePlayer.getByUuid(player.getUniqueId());
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (practicePlayer == null) {
            return;
        }
        if (player.hasPermission("practice.fly")) {
            if (settings.isTime()) {
                player.setAllowFlight(true);
            }
            else {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
        practicePlayer.setCurrentState(PlayerState.LOBBY);
        practicePlayer.setTeamNumber(0);
        if (this.handler.getPartyManager().getParty(player.getUniqueId()) != null) {
            player.getInventory().setContents(Practice.getInstance().getManagerHandler().getItemManager().getPartyItems());
            player.updateInventory();
        }
        else {
            player.getInventory().setContents(Practice.getInstance().getManagerHandler().getItemManager().getSpawnItems());
            if (practicePlayer.isShowRematchItemFlag()) {
                practicePlayer.setShowRematchItemFlag(false);
                final ItemStack itemStack = new ItemStack(Material.BLAZE_POWDER);
                UtilItem.name(itemStack, "§eRematch §7(Right Click)", 15L);
                player.getInventory().setItem(6, itemStack);
            }
            player.updateInventory();
        }
        final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
        if (playerBoard != null) {
            playerBoard.setDefaultSidebar(new LobbyScoreboardProvider(this.handler.getPlugin()), 1L);
            playerBoard.setSidebarVisible(practicePlayer.isScoreboard());
        }
    }
    
    public void sendToLobby(final Player player) {
        Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        PlayerListener.getLastPearl().remove(player.getUniqueId());
        UtilPlayer.clear(player);
        final PracticePlayer practicePlayer = PracticePlayer.getByUuid(player.getUniqueId());
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (practicePlayer == null) {
            return;
        }
        if (player.hasPermission("practice.fly")) {
            if (settings.isTime()) {
                player.setAllowFlight(true);
            }
            else {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
        for (final Player pls : Bukkit.getOnlinePlayers()) {
            pls.hidePlayer(player);
            player.hidePlayer(pls);
            final Settings settings2 = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(pls.getPlayer().getUniqueId()).getSettings();
            if (settings2.isSeeAll()) {
                pls.getPlayer().showPlayer(player);
            }
        }
        practicePlayer.setCurrentState(PlayerState.LOBBY);
        practicePlayer.setTeamNumber(0);
        if (this.handler.getPlugin().getSpawn() != null) {
            player.teleport(this.handler.getPlugin().getSpawn());
        }
        if (this.handler.getPartyManager().getParty(player.getUniqueId()) != null) {
            player.getInventory().setContents(Practice.getInstance().getManagerHandler().getItemManager().getPartyItems());
            player.updateInventory();
        }
        else {
            player.getInventory().setContents(Practice.getInstance().getManagerHandler().getItemManager().getSpawnItems());
            if (practicePlayer.isShowRematchItemFlag()) {
                practicePlayer.setShowRematchItemFlag(false);
                final ItemStack itemStack = new ItemStack(Material.BLAZE_POWDER);
                UtilItem.name(itemStack, "§eRematch §7(Right Click)", 15L);
                player.getInventory().setItem(6, itemStack);
            }
            player.updateInventory();
        }
        player.updateInventory();
        final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
        if (playerBoard != null) {
            playerBoard.setDefaultSidebar(new LobbyScoreboardProvider(this.handler.getPlugin()), 20L);
            playerBoard.setSidebarVisible(practicePlayer.isScoreboard());
        }
    }
    
    public int getGlobalElo(final PracticePlayer player, final boolean solo) {
        int i = 0;
        int count = 0;
        for (final Kit kit : this.handler.getKitManager().getKitMap().values()) {
            if (solo) {
                i += player.getEloMap().get(kit.getName());
                ++count;
            }
        }
        if (i == 0) {
            i = 1;
        }
        if (count == 0) {
            count = 1;
        }
        return Math.round(i / count);
    }
}
