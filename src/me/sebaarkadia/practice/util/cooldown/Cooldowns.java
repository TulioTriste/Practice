// 
// Decompiled by Procyon v0.5.30
// 

package me.sebaarkadia.practice.util.cooldown;

import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;

public class Cooldowns
{
    private static HashMap<String, HashMap<UUID, Long>> cooldowns;
    
    public static void deleteCooldowns() {
        Cooldowns.cooldowns.clear();
    }
    
    public static void createCooldown(final String cooldown) {
        if (Cooldowns.cooldowns.containsKey(cooldown)) {
            throw new IllegalArgumentException("Sorry, but cooldown doesn't exists.");
        }
        Cooldowns.cooldowns.put(cooldown, new HashMap<UUID, Long>());
    }
    
    public static HashMap<UUID, Long> getCooldownMap(final String cooldown) {
        if (Cooldowns.cooldowns.containsKey(cooldown)) {
            return Cooldowns.cooldowns.get(cooldown);
        }
        return null;
    }
    
    public static void addCooldown(final String cooldown, final Player p, final int seconds) {
        if (!Cooldowns.cooldowns.containsKey(cooldown)) {
            throw new IllegalArgumentException(String.valueOf(cooldown) + " doesn't exists.");
        }
        final long next = System.currentTimeMillis() + seconds * 1000L;
        Cooldowns.cooldowns.get(cooldown).put(p.getUniqueId(), next);
    }
    
    public static boolean isOnCooldown(final String cooldown, final Player p) {
        return Cooldowns.cooldowns.containsKey(cooldown) && Cooldowns.cooldowns.get(cooldown).containsKey(p.getUniqueId()) && System.currentTimeMillis() <= Cooldowns.cooldowns.get(cooldown).get(p.getUniqueId());
    }
    
    public static int getCooldownInt(final String cooldown, final Player p) {
        return (int)((Cooldowns.cooldowns.get(cooldown).get(p.getUniqueId()) - System.currentTimeMillis()) / 1000L);
    }
    
    public static long getCooldownLong(final String cooldown, final Player p) {
        return Cooldowns.cooldowns.get(cooldown).get(p.getUniqueId()) - System.currentTimeMillis();
    }
    
    public static void removeCooldown(final String k, final Player p) {
        if (!Cooldowns.cooldowns.containsKey(k)) {
            throw new IllegalArgumentException(String.valueOf(k) + " doesn't exists.");
        }
        Cooldowns.cooldowns.get(k).remove(p.getUniqueId());
    }
    
    static {
        Cooldowns.cooldowns = new HashMap<String, HashMap<UUID, Long>>();
    }
}
