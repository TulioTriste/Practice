package me.sebaarkadia.practice.util;

import org.bukkit.scheduler.BukkitRunnable;

import me.sebaarkadia.practice.Practice;

public class ColorAPI2
{
    public static String prefix;
    public static String suffix;
    
    public static void Color() {
        new BukkitRunnable() {
            int n = 0;
            
            public void run() {
                ++this.n;
                if (this.n == 1) {
                    ColorAPI2.prefix = "§3";
                    ColorAPI2.suffix = "»";
                }
                else if (this.n == 2) {
                    ColorAPI2.prefix = "§b";
                    ColorAPI2.suffix = "»";
                }
                else if (this.n == 3) {
                    ColorAPI2.prefix = "§f";
                    ColorAPI2.suffix = "»";
                    this.n = 0;
                }
            }
        }.runTaskTimerAsynchronously(Practice.getInstance(), 0L, 10L);
    }
}
