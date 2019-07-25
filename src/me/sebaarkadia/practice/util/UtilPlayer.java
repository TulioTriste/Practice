package me.sebaarkadia.practice.util;

import com.google.common.base.Preconditions;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.settings.Settings;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.Iterator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.entity.Player;

public class UtilPlayer
{
    public static void clear(final Player player) {
        for (final PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (settings.isMessage()) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
        player.setMaximumNoDamageTicks(20);
        player.setFoodLevel(20);
        player.setHealth(20.0);
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.setMaximumNoDamageTicks(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.updateInventory();
    }
    
    public static int getPing(final Player player) {
        final int ping = ((CraftPlayer)player).getHandle().ping;
        return ping;
    }
    
    public static void clear2(final Player player) {
        for (final PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.setMaximumNoDamageTicks(20);
        player.setFoodLevel(20);
        player.setHealth(20.0);
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.updateInventory();
    }
    
    public static PotionEffect getPotionEffect(final Player player, final PotionEffectType type) {
        Preconditions.checkState(player.hasPotionEffect(type));
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().getId() != type.getId()) {
                continue;
            }
            return effect;
        }
        throw new AssertionError();
    }
    
    public static void addConcideringLevel(final Player player, final PotionEffect effect) {
        if (player.hasPotionEffect(effect.getType())) {
            final PotionEffect before = getPotionEffect(player, effect.getType());
            if (before.getAmplifier() < effect.getAmplifier()) {
                player.addPotionEffect(effect, true);
            }
            else if (before.getAmplifier() == effect.getAmplifier() && before.getDuration() < effect.getDuration()) {
                player.addPotionEffect(effect, true);
            }
        }
        else {
            player.addPotionEffect(effect);
        }
    }
}
