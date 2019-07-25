package me.sebaarkadia.practice.commands;

import org.bukkit.inventory.meta.ItemMeta;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class ModCommand implements CommandExecutor
{
    private Practice plugin;
    
    public ModCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (!player.hasPermission("practice.commands.mod")) {
            player.sendMessage("No Permission!");
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar ese comando en tu estado"));
            return true;
        }
        if (!settings.isMod()) {
            settings.setMod(!settings.isMod());
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§aModmode is enabled" : "§aModmode esta activado"));
            UtilPlayer.clear2(player);
            final ItemStack item4 = new ItemStack(Material.WOOD_SWORD);
            final ItemMeta itemm4 = item4.getItemMeta();
            itemm4.setDisplayName("§aKnockback Sword");
            itemm4.spigot().setUnbreakable(true);
            item4.setItemMeta(itemm4);
            item4.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
            player.getInventory().setItem(0, item4);
            final ItemStack randomtp = new ItemStack(Material.EYE_OF_ENDER);
            final ItemMeta randomtp2 = randomtp.getItemMeta();
            randomtp2.setDisplayName("§aRandom TP");
            randomtp.setItemMeta(randomtp2);
            player.getInventory().setItem(2, randomtp);
            final ItemStack spawn = new ItemStack(Material.REDSTONE_TORCH_ON);
            final ItemMeta spawn2 = spawn.getItemMeta();
            spawn2.setDisplayName("§cTeleport to spawn");
            spawn.setItemMeta(spawn2);
            player.getInventory().setItem(4, spawn);
            final ItemStack worldedit = new ItemStack(Material.WOOD_AXE);
            final ItemMeta worldedit2 = spawn.getItemMeta();
            worldedit2.setDisplayName("§eWorld Edit");
            worldedit.setItemMeta(worldedit2);
            player.getInventory().setItem(6, worldedit);
            final ItemStack leave = new ItemStack(Material.INK_SACK, 1, (short)1);
            final ItemMeta leave2 = leave.getItemMeta();
            leave2.setDisplayName("§cLeave StaffMode");
            leave.setItemMeta(leave2);
            player.getInventory().setItem(8, leave);
            player.updateInventory();
            player.setAllowFlight(true);
            player.chat("/seeall");
            return true;
        }
        settings.setMod(!settings.isMod());
        if (settings.isSeeAll()) {
            settings.setSeeAll(!settings.isSeeAll());
            for (final Player pls : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(pls);
            }
        }
        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
        player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cModemode is disabled" : "§cModmode desactivadó"));
        return true;
    }
}
