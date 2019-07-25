package me.sebaarkadia.practice.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.Listener;

public class EntityListener implements Listener
{
    private Practice plugin;
    
    public EntityListener(final Practice plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void EntityDamageByEntityEvent(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final Player damagedPlayer = (Player)e.getEntity();
            final Player attackerPlayer = (Player)e.getDamager();
            final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(attackerPlayer.getUniqueId()).getSettings();
            if (settings.isMod()) {
                return;
            }
            final PracticePlayer damagedPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(damagedPlayer);
            final PracticePlayer attackerPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(attackerPlayer);
            if (damagedPracPlayer.getCurrentState() != PlayerState.FIGHTING || attackerPracPlayer.getCurrentState() != PlayerState.FIGHTING) {
                e.setCancelled(true);
            }
            if (damagedPracPlayer.getTeamNumber() == 0) {
                return;
            }
            final int damagedTeamNumber = damagedPracPlayer.getTeamNumber();
            final int attackerTeamNumber = attackerPracPlayer.getTeamNumber();
            if (damagedTeamNumber == attackerTeamNumber) {
                e.setCancelled(true);
                return;
            }
            if (attackerPracPlayer.getCurrentState() == PlayerState.FIGHTING) {
                attackerPracPlayer.setTotalHit(attackerPracPlayer.getTotalHit() + 1);
                attackerPracPlayer.setLongestCombo(attackerPracPlayer.getLongestCombo() + 1);
                if (damagedPracPlayer.getLongestCombo() > damagedPracPlayer.getBestLongestCombo()) {
                    damagedPracPlayer.setBestLongestCombo(damagedPracPlayer.getLongestCombo());
                    damagedPracPlayer.setLongestCombo(0);
                }
                else if (damagedPracPlayer.getLongestCombo() < damagedPracPlayer.getBestLongestCombo()) {
                    damagedPracPlayer.setLongestCombo(0);
                }
            }
        }
    }
    
    @EventHandler
    public void EntityDamageEvent(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer((Player)e.getEntity());
            if (practicePlayer.getCurrentState() == PlayerState.FIGHTING) {
                return;
            }
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void PotionSplashEvent(final PotionSplashEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer((Player)e.getEntity().getShooter());
            if (practicePlayer.getPotion() != 0) {
                practicePlayer.setPotion(practicePlayer.getPotion() - 1);
            }
            if (practicePlayer.getCurrentState() == PlayerState.FIGHTING && !e.getAffectedEntities().contains(e.getEntity().getShooter())) {
                practicePlayer.setPotionMiss(practicePlayer.getPotionMiss() + 1);
            }
        }
    }
}
