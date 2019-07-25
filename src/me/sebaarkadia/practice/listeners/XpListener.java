package me.sebaarkadia.practice.listeners;

import org.bukkit.plugin.Plugin;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.settings.Settings;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;
import java.util.WeakHashMap;
import org.bukkit.event.Listener;

public class XpListener implements Listener
{
    private static WeakHashMap<Player, Long> enderpearlCooldown;
    private static int cooldowntime;
    private double cooldowntimedouble;
    private static float removeThatxp;
    private static boolean xpbarenabled;
    static XpListener instance;
    
    static {
        XpListener.instance = new XpListener();
    }
    
    public XpListener() {
        XpListener.enderpearlCooldown = new WeakHashMap<Player, Long>();
        XpListener.cooldowntime = 14;
        this.cooldowntimedouble = 14.0;
        XpListener.removeThatxp = 0.99f / ((float)(this.cooldowntimedouble * 20.0) / 2.0f);
        XpListener.xpbarenabled = true;
    }
    
    public static XpListener getInstance() {
        return XpListener.instance;
    }
    
    public static boolean isEnderPearlCooldownActive(final Player p) {
        return XpListener.enderpearlCooldown.containsKey(p) && XpListener.enderpearlCooldown.get(p) > System.currentTimeMillis();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void PlayerTeleportEvent(final PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            final Location location = event.getTo();
            location.setX(location.getBlockX() + 0.5);
            location.setY((double)location.getBlockY());
            location.setZ(location.getBlockZ() + 0.5);
            event.setTo(location);
        }
    }
    
    public static void applyCooldown(final Player p) {
        XpListener.enderpearlCooldown.put(p, System.currentTimeMillis() + XpListener.cooldowntime * 1000);
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(p.getUniqueId()).getSettings();
        if (XpListener.xpbarenabled) {
            p.setExp(0.99f);
            new BukkitRunnable() {
                @SuppressWarnings("unused")
				public void run() {
                    if (p != null) {
                        if (XpListener.isEnderPearlCooldownActive(p)) {
                            if (p.getGameMode() != GameMode.CREATIVE) {
                                p.setExp(p.getExp() - XpListener.removeThatxp);
                            }
                            else {
                                XpListener.removeCooldown(p);
                                this.cancel();
                            }
                        }
                        else {
                            XpListener.removeCooldown(p);
                            this.cancel();
                        }
                    }
                    else {
                        p.sendMessage(String.valueOf(settings.isPublicChat() ? "§9You can Enderpearl!" : "§9Puedes usar la Enderpearl!"));
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously((Plugin)Practice.getInstance(), 0L, 2L);
        }
    }
    
    public static void removeCooldown(final Player p) {
        if (XpListener.enderpearlCooldown.containsKey(p)) {
            XpListener.enderpearlCooldown.remove(p);
            p.setLevel(0);
            p.setExp(0.0f);
        }
    }
}
