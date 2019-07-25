package me.sebaarkadia.practice.scoreboard;

import java.util.List;

import org.bukkit.Bukkit;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.duel.Duel;
import me.sebaarkadia.practice.util.PlayerUtility;

public class PlayerBoard
{
    public final BufferedObjective bufferedObjective;
    private final Team teammates;
    private final Team opponents;
    private final Team members;
    private final Scoreboard scoreboard;
    private final Player player;
    private final Practice plugin;
    @SuppressWarnings("unused")
	private boolean sidebarVisible;
    private boolean removed;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;
    
    public PlayerBoard(final Practice plugin, final Player player) {
        this.sidebarVisible = false;
        this.removed = false;
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        (this.members = this.scoreboard.registerNewTeam("members")).setPrefix(ChatColor.WHITE.toString());
        (this.teammates = this.scoreboard.registerNewTeam("teammates")).setPrefix(ChatColor.GREEN.toString());
        (this.opponents = this.scoreboard.registerNewTeam("opponents")).setPrefix(ChatColor.RED.toString());
        player.setScoreboard(this.scoreboard);
    }
    
    public void remove() {
        this.removed = true;
        if (this.scoreboard != null) {
            synchronized (this.scoreboard) {
                for (final Team team : this.scoreboard.getTeams()) {
                    team.unregister();
                }
                for (final Objective objective : this.scoreboard.getObjectives()) {
                    objective.unregister();
                }
            }
            // monitorexit(this.scoreboard)
        }
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void setSidebarVisible(final boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }
    
    public void setDefaultSidebar(final SidebarProvider provider, final long updateInterval) {
        if (provider != null && provider.equals(this.defaultProvider)) {
            return;
        }
        this.defaultProvider = provider;
        if (this.runnable != null) {
            this.runnable.cancel();
        }
        if (provider == null) {
            this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        (this.runnable = new BukkitRunnable() {
            public void run() {
                if (PlayerBoard.this.removed) {
                    this.cancel();
                    return;
                }
                if (provider.equals(PlayerBoard.this.defaultProvider)) {
                    PlayerBoard.this.updateObjective();
                }
            }
        }).runTaskTimerAsynchronously((Plugin)this.plugin, 0L, 20L);
    }
    
    private void updateObjective() {
        final SidebarProvider provider = (this.temporaryProvider != null) ? this.temporaryProvider : this.defaultProvider;
        if (provider == null) {
            this.bufferedObjective.setVisible(false);
        }
        else {
            this.bufferedObjective.setTitle(provider.getTitle(this.player));
            this.bufferedObjective.setAllLines(provider.getLines(this.player));
            this.bufferedObjective.flip();
        }
    }
    
    public void addUpdates(final Player playerInDuel) {
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
			public void run() {
                final Duel currentDuel = PlayerBoard.this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(playerInDuel.getUniqueId());
                final List<UUID> selfTeam = (currentDuel == null) ? null : currentDuel.getDuelTeam(playerInDuel);
                final List<UUID> otherTeam = (currentDuel == null) ? null : currentDuel.getOtherDuelTeam(playerInDuel);
                if (selfTeam != null) {
                    for (final UUID uuid : selfTeam) {
                        final Player target = Bukkit.getPlayer(uuid);
                        if (target != null && !PlayerBoard.this.teammates.hasPlayer(target)) {
                            PlayerBoard.this.teammates.addPlayer(target);
                        }
                    }
                }
                if (otherTeam != null) {
                    for (final UUID uuid : otherTeam) {
                        final Player target = Bukkit.getPlayer(uuid);
                        if (target != null && !PlayerBoard.this.opponents.hasPlayer(target)) {
                            PlayerBoard.this.opponents.addPlayer(target);
                        }
                    }
                }
                if (currentDuel == null) {
                    for (final Player online : PlayerUtility.getOnlinePlayers()) {
                        if (!PlayerBoard.this.members.hasPlayer(online)) {
                            PlayerBoard.this.members.addPlayer(online);
                        }
                    }
                }
            }
        }.runTask((Plugin)this.plugin);
    }
}
