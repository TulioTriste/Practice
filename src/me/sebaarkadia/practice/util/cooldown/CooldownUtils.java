package me.sebaarkadia.practice.util.cooldown;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

public class CooldownUtils
{
  @SuppressWarnings({ "unchecked", "rawtypes" })
private static HashMap<String, HashMap<UUID, Long>> cooldown = new HashMap();
  
  public static void clearCooldowns()
  {
    cooldown.clear();
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static void createCooldown(String k)
  {
    if (cooldown.containsKey(k)) {
      throw new IllegalArgumentException("Ce cooldown existe d?j?");
    }
    cooldown.put(k, new HashMap());
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static HashMap<UUID, Long> getCooldownMap(String k)
  {
    if (cooldown.containsKey(k)) {
      return (HashMap)cooldown.get(k);
    }
    return null;
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static void addCooldown(String k, Player p, int seconds)
  {
    if (!cooldown.containsKey(k)) {
      throw new IllegalArgumentException(String.valueOf(k) + " n'existe pas");
    }
    long next = System.currentTimeMillis() + seconds * 1000L;
    ((HashMap)cooldown.get(k)).put(p.getUniqueId(), Long.valueOf(next));
  }
  @SuppressWarnings({ "rawtypes" })
  public static boolean isOnCooldown(String k, Player p)
  {
    return (cooldown.containsKey(k)) && (((HashMap)cooldown.get(k)).containsKey(p.getUniqueId())) && (System.currentTimeMillis() <= ((Long)((HashMap)cooldown.get(k)).get(p.getUniqueId())).longValue());
  }
  @SuppressWarnings({ "rawtypes" })
  public static int getCooldownForPlayerInt(String k, Player p)
  {
    return (int)((((Long)((HashMap)cooldown.get(k)).get(p.getUniqueId())).longValue() - System.currentTimeMillis()) / 1000L);
  }
  @SuppressWarnings({ "rawtypes" })
  public static long getCooldownForPlayerLong(String k, Player p)
  {
    return ((Long)((HashMap)cooldown.get(k)).get(p.getUniqueId())).longValue() - System.currentTimeMillis();
  }
  @SuppressWarnings({ "rawtypes" })
  public static void removeCooldown(String k, Player p)
  {
    if (!cooldown.containsKey(k)) {
      throw new IllegalArgumentException(String.valueOf(k) + " n'existe pas");
    }
    ((HashMap)cooldown.get(k)).remove(p.getUniqueId());
  }
}
