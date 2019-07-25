package me.sebaarkadia.practice.scoreboard;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.scoreboard.sidebar.LobbyScoreboardProvider;
import me.sebaarkadia.practice.util.PlayerUtility;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import org.bukkit.event.Listener;

public class ScoreboardHandler implements Listener
{
    private Map<UUID, PlayerBoard> playerBoards;
    private Practice plugin;
    private SidebarProvider defaultProvider;
    
    public ScoreboardHandler(final Practice plugin) {
        this.playerBoards = new HashMap<UUID, PlayerBoard>();
        this.plugin = plugin;
        this.defaultProvider = new LobbyScoreboardProvider(this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        for (final Player player : PlayerUtility.getOnlinePlayers()) {
            final PlayerBoard playerBoard = new PlayerBoard(plugin, player);
            this.setPlayerBoard(player.getUniqueId(), playerBoard);
            playerBoard.addUpdates(player);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final PlayerBoard board2 = new PlayerBoard(this.plugin, player);
        this.setPlayerBoard(uuid, board2);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }
    
    public PlayerBoard getPlayerBoard(final UUID uuid) {
        return this.playerBoards.get(uuid);
    }
    
    public void setPlayerBoard(final UUID uuid, final PlayerBoard board) {
        this.playerBoards.put(uuid, board);
        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.defaultProvider, 15L);
    }
    
    public void clearBoards() {
        final Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
}
