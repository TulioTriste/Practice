package me.sebaarkadia.practice.listeners;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.arena.Arena;
import me.sebaarkadia.practice.duel.Duel;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;

import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockSpreadEvent;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import java.util.ArrayList;

import org.bukkit.event.Listener;

public class BlockListener implements Listener
{
    private Practice plugin;
    public static ArrayList<Block> blocks;
    
    static {
        BlockListener.blocks = new ArrayList<Block>();
    }
    
    public BlockListener(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public static List<Block> getNearbyBlocks(final Location location, final int radius) {
        final List<Block> blocks = new ArrayList<Block>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; ++x) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; ++y) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; ++z) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
    
    @EventHandler
    public void onBlockForm(final BlockSpreadEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent e) {
        final Arena arena = this.plugin.getManagerHandler().getArenaManager().closest(e.getBlockClicked().getLocation());
        if (arena != null) {
            arena.getBlockChangeTracker().add(e.getBlockClicked().getRelative(e.getBlockFace()).getState());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockForm(final BlockFormEvent event) {
        final Arena arena = this.plugin.getManagerHandler().getArenaManager().closest(event.getBlock().getLocation());
        if (arena != null) {
            arena.getBlockChangeTracker().add(event.getBlock().getState());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockForm(final BlockFromToEvent event) {
        final Arena arena = this.plugin.getManagerHandler().getArenaManager().closest(event.getToBlock().getLocation());
        if (arena != null) {
            arena.getBlockChangeTracker().add(event.getToBlock().getState());
            arena.getBlockChangeTracker().add(event.getToBlock().getRelative(BlockFace.DOWN).getState());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() == PlayerState.BUILDER) {
            return;
        }
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(e.getPlayer().getUniqueId());
        final Arena arena;
        if (duel != null && (arena = this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName())).getBlockChangeTracker().isPlayerPlacedBlock(e.getBlock().getLocation())) {
            arena.getBlockChangeTracker().add(e.getBlock().getState());
            return;
        }
        e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() == PlayerState.BUILDER) {
            return;
        }
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(e.getPlayer().getUniqueId());
        if (duel == null) {
            e.setCancelled(true);
            return;
        }
        final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName());
        final double averageY = (arena.getFirstTeamLocation().getY() + arena.getSecondTeamLocation().getY()) / 2.0;
        if (Math.abs(e.getBlock().getY() - averageY) > 5.0) {
            e.setCancelled(true);
            return;
        }
        arena.getBlockChangeTracker().setPlayerPlacedBlock(e.getBlock().getLocation());
        arena.getBlockChangeTracker().add(e.getBlockReplacedState());
    }
}
