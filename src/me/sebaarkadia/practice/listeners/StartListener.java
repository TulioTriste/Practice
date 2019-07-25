package me.sebaarkadia.practice.listeners;

import org.bukkit.plugin.Plugin;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

import me.sebaarkadia.practice.Practice;

import org.bukkit.entity.Player;
import java.util.WeakHashMap;
import org.bukkit.event.Listener;

public class StartListener implements Listener
{
    private static WeakHashMap<Player, Long> startcooldown;
    private static int cooldowntime;
    private double cooldowntimedouble;
    private static float removeThatxp;
    private static boolean xpbarenabled;
    static StartListener instance;
    
    static {
        StartListener.instance = new StartListener();
    }
    
    public StartListener() {
        StartListener.startcooldown = new WeakHashMap<Player, Long>();
        StartListener.cooldowntime = 5;
        this.cooldowntimedouble = 5.0;
        StartListener.removeThatxp = 0.99f / ((float)(this.cooldowntimedouble * 20.0) / 2.0f);
        StartListener.xpbarenabled = true;
    }
    
    public static StartListener getInstance() {
        return StartListener.instance;
    }
    
    public static boolean isStartCooldown(final Player p) {
        return StartListener.startcooldown.containsKey(p) && StartListener.startcooldown.get(p) > System.currentTimeMillis();
    }
    
    public static void applyCooldown(final Player p) {
        StartListener.startcooldown.put(p, System.currentTimeMillis() + StartListener.cooldowntime * 1000);
        if (StartListener.xpbarenabled) {
            p.setExp(0.99f);
            new BukkitRunnable() {
                public void run() {
                    if (p != null) {
                        if (StartListener.isStartCooldown(p)) {
                            if (p.getGameMode() != GameMode.CREATIVE) {
                                p.setExp(p.getExp() - StartListener.removeThatxp);
                            }
                            else {
                                StartListener.removeCooldown(p);
                                this.cancel();
                            }
                        }
                        else {
                            StartListener.removeCooldown(p);
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimerAsynchronously(Practice.getInstance(), 0L, 2L);
        }
    }
    
    public static void removeCooldown(final Player p) {
        if (StartListener.startcooldown.containsKey(p)) {
            StartListener.startcooldown.remove(p);
            p.setLevel(0);
            p.setExp(0.0f);
        }
    }
}
