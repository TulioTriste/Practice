package me.sebaarkadia.practice.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

public class PlayerUtil {

	public static int getPing(Player player) {
		int ping = ((CraftPlayer) player).getHandle().ping;

		if (ping >= 100) {
			return ping - 30;
		}

		if (ping >= 50) {
			return ping - 20;
		}

		if (ping >= 20) {
			return ping - 10;
		}

		return ping;
	}

    public static void sendMessage(final String message, final Player... players) {
        for (final Player player : players) {
            player.sendMessage(message);
        }
    }

    public static void sendMessage(final String message, final Set<Player> players) {
        for (final Player player : players) {
            player.sendMessage(message);
        }
    }
	
    public static void clearPlayer(final Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(12.8f);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setWalkSpeed(0.2f);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
        player.updateInventory();
    }

    public static void sendFirework(final FireworkEffect effect, final Location location) {
        final Firework f = (Firework) location.getWorld().spawn(location, Firework.class);
        final FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(effect);
        f.setFireworkMeta(fm);
        try {
            final Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
            final Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            final Object firework = craftFireworkClass.cast(f);
            final Method handle = firework.getClass().getMethod("getHandle", (Class<?>[]) new Class[0]);
            final Object entityFirework = handle.invoke(firework);
            final Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            final Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Class<?> getClass(final String prefix, final String nmsClassString) throws ClassNotFoundException {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        final String name = prefix + version + nmsClassString;
        final Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }
	
}
