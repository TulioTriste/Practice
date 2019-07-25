package me.sebaarkadia.practice.player;

import org.bukkit.entity.Damageable;
import java.util.List;
import java.util.Arrays;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;

import org.bukkit.Material;
import org.apache.commons.lang3.StringEscapeUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.util.UtilItem;
import me.sebaarkadia.practice.util.UtilMath;
import me.sebaarkadia.practice.util.UtilString;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class PlayerInventorySnapshot
{
    private String playerName;
    private UUID uuid;
    private ItemStack[] mainContent;
    private ItemStack[] armorContent;
    private Collection<PotionEffect> potionEffects;
    private int food;
    private double health;
    private int missedPotions;
    private int longestCombo;
    private int totalHits;
    private int potion;
    private Inventory inventory;
    
    public PlayerInventorySnapshot(final Player player) {
        this.playerName = player.getName();
        this.uuid = player.getUniqueId();
        this.mainContent = player.getInventory().getContents();
        this.armorContent = player.getInventory().getArmorContents();
        this.potionEffects = (Collection<PotionEffect>)player.getActivePotionEffects();
        this.food = player.getFoodLevel();
        this.health = ((Damageable)player).getHealth();
        final PracticePlayer practicePlayer = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        practicePlayer.setLastMissedPotions(practicePlayer.getPotionMiss());
        this.missedPotions = practicePlayer.getLastMissedPotions();
        if (practicePlayer.getLongestCombo() > practicePlayer.getBestLongestCombo()) {
            practicePlayer.setBestLongestCombo(practicePlayer.getLongestCombo());
        }
        practicePlayer.setLastBestLongestCombo(practicePlayer.getBestLongestCombo());
        this.potion = practicePlayer.getPotion();
        this.longestCombo = practicePlayer.getLastBestLongestCombo();
        practicePlayer.setLastTotalHit(practicePlayer.getTotalHit());
        this.totalHits = practicePlayer.getLastTotalHit();
        new BukkitRunnable() {
            public void run() {
                practicePlayer.setPotionMiss(0);
                practicePlayer.setLongestCombo(0);
                practicePlayer.setBestLongestCombo(0);
                practicePlayer.setPotion(0);
                practicePlayer.setBestLongestCombo(0);
                practicePlayer.setTotalHit(0);
            }
        }.runTaskLater((Plugin)Practice.getInstance(), 10L);
        this.initInventory();
    }
    
    private void initInventory() {
        this.inventory = Bukkit.createInventory((InventoryHolder)null, 54, String.valueOf(String.valueOf(this.playerName)) + "'s inventory");
        final String enabled = ChatColor.GRAY + " " + StringEscapeUtils.unescapeHtml4("&#9658;") + " ";
        final double roundedHealth = Math.round(this.health / 2.0 * 2.0) / 2.0;
        final ItemStack skull = UtilItem.createItem(Material.SKULL_ITEM, (int)Math.round(this.health), (short)0, "§9" + roundedHealth + " §fHP");
        this.inventory.setItem(52, skull);
        final ItemStack melon = UtilItem.createItem(Material.COOKED_BEEF, this.food, (short)0, "§9" + this.food + " §fHunger");
        this.inventory.setItem(53, melon);
        final List<String> lores = new ArrayList<String>();
        for (final PotionEffect effect : this.potionEffects) {
            final int duration = effect.getDuration();
            final String durationMinuteSecond = UtilMath.convertTicksToMinutes(duration);
            final String effectAmplifierRoman = UtilString.romanNumerals(effect.getAmplifier() + 1);
            String effectName = effect.getType().getName().toLowerCase();
            effectName = effectName.replace('_', ' ');
            effectName = WordUtils.capitalizeFully(effectName);
            lores.add(String.valueOf(String.valueOf(enabled)) + "§f" + effectName + " " + effectAmplifierRoman + ChatColor.GRAY + " (" + durationMinuteSecond + ")");
        }
        final ItemStack brewingStand = UtilItem.createItem(Material.BREWING_STAND_ITEM, this.potionEffects.size(), (short)0, "§9Potion Effects", lores);
        this.inventory.setItem(49, brewingStand);
        final ItemStack stats = UtilItem.createItem(Material.PAPER, 1, (short)0, "§9Stats", Arrays.asList(String.valueOf(String.valueOf(enabled)) + "§fLongest Combo: §9" + this.longestCombo + " Hits", String.valueOf(String.valueOf(enabled)) + "§fTotal Hits: §9" + this.totalHits + " Hits"));
        this.inventory.setItem(46, stats);
        for (int j = 0; j < 36; ++j) {
            if (this.mainContent[j] != null) {
                this.inventory.setItem(j, this.mainContent[j]);
            }
        }
        for (int j = 0; j < 9; ++j) {
            this.inventory.setItem(j + 27, this.inventory.getItem(j));
        }
        for (int j = 0; j < 18; ++j) {
            this.inventory.setItem(j, this.inventory.getItem(j + 9));
        }
        for (int j = 36; j <= 39; ++j) {
            if (this.armorContent[39 - j] != null) {
                this.inventory.setItem(j, this.armorContent[39 - j]);
            }
        }
        final ItemStack healthPot = UtilItem.createItem(Material.POTION, this.potion, (short)16421, "§9Potions:", Arrays.asList(String.valueOf(String.valueOf(ChatColor.LIGHT_PURPLE.toString())) + enabled + "§fTotal Potions: §9" + this.potion, String.valueOf(String.valueOf(enabled)) + "§fMissed Potions: §9" + this.missedPotions));
        this.inventory.setItem(45, healthPot);
    }
    
    public Collection<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }
    
    public ItemStack[] getArmorContent() {
        return this.armorContent;
    }
    
    public ItemStack[] getMainContent() {
        return this.mainContent;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
}
