package me.sebaarkadia.practice.util;

import org.bukkit.scheduler.BukkitRunnable;

import me.sebaarkadia.practice.Practice;

public class ColorAPI
{
    public static String prefix;
    public static String suffix;
    
    public static void Color() {
        new BukkitRunnable() {
            int n = 0;
            
            public void run() {
                ++this.n;
                if (this.n == 1) {
                    ColorAPI.prefix = "§6";
                    ColorAPI.suffix = "...";
                }
                else if (this.n == 2) {
                    ColorAPI.prefix = "§6";
                    ColorAPI.suffix = "..";
                }
                else if (this.n == 3) {
                    ColorAPI.prefix = "§6";
                    ColorAPI.suffix = ".";
                    this.n = 0;
                }
            }
        }.runTaskTimerAsynchronously(Practice.getInstance(), 0L, 10L);
    }
}
